package com.lostofthought.lostbot;

// After trying to make multiple accounts, usernames cannot contain '@:#'
// Looks like everything else is fair game. (Note: I did not test control characters, BKSP, ect.)

import com.lostofthought.util.Cast;
import com.lostofthought.util.Exceptional;
import com.lostofthought.util.Func;
import com.lostofthought.util.RegexUtil;
import com.lostofthought.util.functional.*;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.ExtendedInvite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.json.response.ErrorResponse;
import discord4j.rest.util.AllowedMentions;
import discord4j.rest.util.Permission;
import picocli.CommandLine;

import java.util.HashMap;

public class DisUtil {
  public static final RegexUtil.GroupedMatcher UserSnowflakeMatcher = new RegexUtil.GroupedMatcher("^\\s*<@!(\\d+)>\\s*$");
  public static Either<Exception, Snowflake> UserSnowflakeFromMentionString(String s){
    String[][] matches = UserSnowflakeMatcher.Match(s);
    if(matches.length == 1 && matches[0].length == 2){
      return Either.right(Snowflake.of(matches[0][1]));
    }
    return Either.left(new Exception("Invalid user mention string"));
  }
  public static final RegexUtil.GroupedMatcher RoleSnowflakeMatcher = new RegexUtil.GroupedMatcher("^\\s*<@!(\\d+)>\\s*$");
  public static Either<Exception, Snowflake> RoleSnowflakeFromMentionString(String s){
    String[][] matches = RoleSnowflakeMatcher.Match(s);
    if(matches.length == 1 && matches[0].length == 2){
      return Either.right(Snowflake.of(matches[0][1]));
    }
    return Either.left(new Exception("Invalid role mention string"));
  }
  static Either<Exception, User> UserFromMention(GatewayDiscordClient gateway, String mention) {
    return UserSnowflakeFromMentionString(mention).bind(s -> {
      try {
        return Optional.ofJava(gateway.getUserById(s).blockOptional()).reduce(
            () -> Either.left(new Exception("<@!" + mention + "> not found")),
            Either::right
        );
      } catch (Exception e) {
        return Either.left(e);
      }
    });
  }
  static Either<Exception, Role> RoleFromMention(GatewayDiscordClient gateway, Guild g, String mention) {
    return RoleSnowflakeFromMentionString(mention).bind(s -> {
      try {
        return Optional.ofJava(gateway.getRoleById(g.getId(), s).blockOptional()).reduce(
            () -> Either.left(new Exception("<@&" + mention + "> not found")),
            Either::right
        );
      } catch (Exception e) {
        return Either.left(e);
      }
    });
  }
  public static Either<Exception, Member> Member(Snowflake guildSnowflake, User u) {
    try {
      return Optional.ofJava(u.asMember(guildSnowflake).blockOptional()).reduce(
          () -> Either.left(new Exception(u.getTag() + " not found as Member of Guild")),
          Either::right
      );
    } catch (Exception e) {
      return Either.left(e);
    }
  }
  public static String UserAsString(User u){
    return "@" + u.getUsername() + "#" + u.getDiscriminator() + " `<@" + u.getId().asString() + ">`";
  }
  public static String MemberAsString(Member m){
    return "@" + m.getDisplayName() + "#" + m.getDiscriminator() + " `" + m.getUsername() + "#" + m.getDiscriminator() + " <@" + m.getId().asString() + ">`";
  }
  public static Either<Exception, Member> MemberFromMention(GatewayDiscordClient gateway, Snowflake guildSnowflake, String mention) {
    return UserFromMention(gateway, mention).bind(u -> Member(guildSnowflake, u));
  }
  public static MapUtil.WrappedMap<String, Either<Exception, Member>> MentionsAsMembers(GatewayDiscordClient client, Guild guild, String[] mentions){
    return MapUtil.asWrapped(ArrayUtil.asWrapped(mentions).reduce(new HashMap<>(), (acc, m) -> {
      acc.put(m, DisUtil.MemberFromMention(client, guild.getId(), m));
      return acc;
    }));
  }
  public static MapUtil.WrappedMap<String, Either<Exception, User>> MentionsAsUsers(GatewayDiscordClient client, String[] mentions){
    return MapUtil.asWrapped(ArrayUtil.asWrapped(mentions).reduce(new HashMap<>(), (acc, m) -> {
      acc.put(m, DisUtil.UserFromMention(client, m));
      return acc;
    }));
  }
  public static boolean VerifyPermission(Member m, Permission p){
    return Exceptional.eitherOfExceptional(() -> m.getBasePermissions().block()).reduce(
        e -> false,
        perms -> perms.contains(p)
    );
  }

  public static String MentionUser(String s){
    return "<@!" + s + ">";
  }
  public static String MentionUser(User u){
    return "<@!" + u.getId().asString() + ">";
  }

  public static String MentionUser(Member m){
    return "<@!" + m.getId().asString() + ">";
  }

  public static String MentionRoll(Role r){
    return "<@&" + r.getId().asString() + ">";
  }

  public static String ExceptionParser(Exception e){
    return Cast.optionOfChecked(e, ClientException.class).reduce(
        () -> e.getMessage().substring(0, 30) + "...",
        ce -> Optional.ofJava(ce.getErrorResponse()).reduce(
            () -> "Unknown - " + ce.getStatus(),
            er -> MapUtil.asWrapped(er.getFields()).reduce(new StringBuilder(), (a, kv) -> {
              a.append(kv._1);
              a.append(" - ");
              a.append(kv._2);
              a.append("\n");
              return a;
            }).toString().trim()
        )
    );
  }

  public static MessageChannel MessageChannelFromEvent(MessageCreateEvent event){
    return event.getMessage().getChannel().block();
  }

  public static Either<Exception, Message> SendMessage(MessageChannel c, String s) {
    return Exceptional.eitherOfExceptional(() -> c.createMessage(s).block());
  }
  public static Either<Exception, Message> SendMessage(MessageCreateEvent event, String s) {
    return Exceptional.<Exception, MessageChannel>eitherOfExceptional(
        () -> event.getMessage().getChannel().block()
    ).bind(c -> Cast.unchecked(SendMessage(c, s)));
  }
  public static Either<Exception, Message> SendPrivate(User u, String message){
    return Exceptional.eitherOfExceptional(
        () -> u.getPrivateChannel().block()
      ).reduce(
        Either::left,
        pc -> SendMessage(pc, message)
      );
  }


  public static class CLIUser extends Pair<String, Either<Exception, User>>{
    private CLIUser(String first, Either<Exception, User> second) {
      super(first, second);
    }
    public static CommandLine.ITypeConverter<CLIUser> CLIUserFromMention(GatewayDiscordClient gateway){
      return s -> new CLIUser(s, UserFromMention(gateway, s));
    }
    public static Pair<String, Either<Exception, User>> asPair(CLIUser o){
      return o;
    }
  }
  public static class CLIMember extends Pair<String, Either<Exception, Member>>{
    private CLIMember(String first, Either<Exception, Member> second) {
      super(first, second);
    }
    public static CommandLine.ITypeConverter<CLIMember> CLIMemberFromMention(GatewayDiscordClient gateway, Snowflake guildSnowflake){
      return s -> new CLIMember(s, MemberFromMention(gateway, guildSnowflake, s));
    }
    public static Pair<String, Either<Exception, Member>> asPair(CLIMember o){
      return o;
    }
  }

  public static class OneTimeInvite {
    final ExtendedInvite invite;
    private OneTimeInvite(ExtendedInvite invite){
      this.invite = invite;
    }
    public static OneTimeInvite Gen(Guild g) {
      return new OneTimeInvite(ListUtil.asWrapped(g.getChannels().collectList().block())
          .filter(c -> c.getClass() == TextChannel.class)
          .map(Cast::<TextChannel>unchecked)
          .reduce(Pair.from(Integer.MAX_VALUE, (TextChannel) null),
              (acc, tc) -> {
                if (tc.getRawPosition() < acc._1) {
                  acc = Pair.from(tc.getRawPosition(), tc);
                }
                return acc;
              }
          )._2.createInvite(
              s -> {
                s.setMaxUses(1);
                s.setUnique(true);
              }).block());
    }
    public void DeleteInvite(){
      this.invite.delete().block();
    }
    @Override
    public String toString() {
      return "https://discord.gg/" + invite.getCode();
    }
  }


  public static Either<Exception, Guild> GetGuild(MessageCreateEvent e){
    return Exceptional.eitherOfExceptional(
        () -> Optional.of(e.getGuild().block()).coerceNull_().reduce(
            () -> {throw new RuntimeException("Guild not found");},
            g -> g
        )
    );
  }

  public static String ForEachUser(Func.Func1<? super User, ? extends Either<Exception, String>> f, CLIUser[] mentionObjects) {
    return MapUtil.fromPairArray(mentionObjects).map((pair) -> Pair.from(pair._2.reduce(
        ex -> pair._1.replace("@", "[at]"),
        member -> pair._1
    ), pair._2)).map(
        (mention, either) -> either.reduce(
            DisUtil::ExceptionParser,
            f
        )
    ).reduce(new StringBuilder(), (acc, res) -> {
          acc.append(res._1).append(": ").append(res._2).append("\n");
          return acc;
        }
    ).toString();
  }

  public static <T> MapUtil.WrappedMap<String /*mention*/, Either<Exception, T>> ForEachUserT(Func.Func2<String, ? super User, ? extends T> f, CLIUser[] mentionObjects) {
    return MapUtil.fromPairArray(mentionObjects).map(
        (mentionString, userOrException) -> userOrException.map(
            f.apply(mentionString)
        )
    );
  }

  public static String ForEachMember(Func.Func1<? super Member, ? extends Either<Exception, String>> f, CLIMember[] mentionObjects) {
    return MapUtil.fromPairArray(mentionObjects).map((pair) -> Pair.from(pair._2.reduce(
        ex -> pair._1.replace("@", "[at]"),
        member -> pair._1
    ), pair._2)).map(
        (mention, either) -> either.reduce(
            DisUtil::ExceptionParser,
            f
        )
    ).reduce(new StringBuilder(), (acc, res) -> {
          acc.append(res._1).append(": ").append(res._2).append("\n");
          return acc;
        }
    ).toString();
  }

  public static <T> MapUtil.WrappedMap<String /*mention*/, Either<Exception, T>> ForEachMemberT(Func.Func2<String, Member, T> f, CLIMember[] mentionObjects) {
    return MapUtil.fromPairArray(mentionObjects).map(
        (mentionString, userOrException) -> userOrException.map(
            f.apply(mentionString)
        )
    );
  }

  public static <T> MapUtil.WrappedMap<String /*mention*/, Either<Exception, T>> ForEachMemberBindT(Func.Func2<String, Member, Either<Exception, T>> f, CLIMember[] mentionObjects) {
    return MapUtil.fromPairArray(mentionObjects).map(
        (mentionString, userOrException) -> userOrException.bind(
            f.apply(mentionString)
        )
    );
  }

  public static class EmbedField {
    public boolean inline = false;
    public String name = "";
    public String value = "";
    public static EmbedField of(String value, String name, boolean inline){
      EmbedField ef = new EmbedField();
      ef.value = value;
      ef.name = name;
      ef.inline = inline;
      return ef;
    }
    public static EmbedField of(String value, String name){
      EmbedField ef = new EmbedField();
      ef.value = value;
      ef.name = name;
      return ef;
    }
    public static EmbedField of(String value){
      EmbedField ef = new EmbedField();
      ef.value = value;
      return ef;
    }
  }

  public static <T> void ChatUserExceptionMap(MessageChannel c, MapUtil.WrappedMap<String, Either<Exception, EmbedField>> m){
    c.createMessage(ms -> {
      ms.setEmbed(es -> m.map((mention, eepair) -> eepair.reduce(e -> {
        EmbedField ef = new EmbedField();
        ef.name = mention;
        ef.value = "Exception: \n" + DisUtil.ExceptionParser(e);
        return ef;
      }, ef -> ef)).map((dontcare, embed) -> {
        es.addField(embed.name, embed.value, embed.inline);
        return 1;
      }));
    }).transform(
        msg -> msg
    ).block();
  }
  public static <T> void ChatMergeUserExceptionMapAutoName(MessageChannel c, MapUtil.WrappedMap<String, Either<Exception, EmbedField>> m){
    ChatUserExceptionMap(c, m.map((mention, eepair) -> eepair.map(ef -> {
      ef.name = mention;
      return ef;
    })));
  }
  public static <T> void ChatUserExceptionMap(MessageCreateEvent e, MapUtil.WrappedMap<String, Either<Exception, EmbedField>> m){
    ChatUserExceptionMap(DisUtil.MessageChannelFromEvent(e), m);
  }
  public static void ChatMergeUserExceptionMapAutoName(MessageCreateEvent e, MapUtil.WrappedMap<String, Either<Exception, EmbedField>> m){
    ChatMergeUserExceptionMapAutoName(DisUtil.MessageChannelFromEvent(e), m);
  }
}
