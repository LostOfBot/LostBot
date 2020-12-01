package com.lostofthought.lostbot;

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
    return "Exception - "
        + Cast.optionOfChecked(e, ClientException.class).reduce(
        e::getMessage,
        ce -> Optional.ofJava(ce.getErrorResponse()).reduce(
            () -> "Unknown - " + ce.getStatus(),
            ErrorResponse::toString
        )
    );
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

  public static String ForEachMember(Func.Func1<Member, Either<Exception, String>> f, CLIMember[] mentionObjects) {
    return MapUtil.asWrapped(ArrayUtil.asWrapped(mentionObjects).map(CLIMember::asPair).reduce(
        new HashMap<String, Either<Exception, Member>>(),
        (acc, p) -> {
          acc.put(p._1, p._2);
          return acc;
        }
    )).map((pair) -> Pair.from(pair._2.reduce(
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
    return MapUtil.asWrapped(ArrayUtil.asWrapped(mentionObjects).map(CLIUser::asPair).reduce(
        new HashMap<String, Either<Exception, User>>(),
        (acc, p) -> {
          acc.put(p._1, p._2);
          return acc;
        }
    )).map((pair) -> Pair.from(pair._2.reduce(
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
}
