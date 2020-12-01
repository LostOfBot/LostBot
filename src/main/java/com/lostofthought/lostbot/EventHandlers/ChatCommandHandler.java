package com.lostofthought.lostbot.EventHandlers;

import com.lostofthought.lostbot.ChatWriter;
import com.lostofthought.lostbot.DisUtil;
import com.lostofthought.util.CLI;
import com.lostofthought.util.Cast;
import com.lostofthought.util.Exceptional;
import com.lostofthought.util.functional.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.BanQuerySpec;
import discord4j.rest.util.Permission;
import picocli.CommandLine;

import java.time.Instant;
import java.util.HashMap;

public class ChatCommandHandler {
  public static final String commandPrefix = "?";

  @CommandLine.Command(name = commandPrefix, subcommands = {CommandLine.HelpCommand.class},
      description = "LostBot is the multi-purpose bot developed by `LostOfThought#0001`")
  public static class MessageCommand implements Runnable {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;
    private final MessageCreateEvent event;
    private final Either<Exception, Member> requestingMember;
    private final Either<Exception, Guild> currentGuild;
    private final Either<Exception, Channel> currentChannel;

    MessageCommand(MessageCreateEvent event, Either<Exception, Guild> g) {
      this.event = event;
      this.requestingMember = Optional.ofJava(event.getMember()).reduce(
          () -> Either.left(new RuntimeException("Requesting member not found")),
          Either::right
      );
      this.currentChannel = Exceptional.eitherOfExceptional(() -> event.getMessage().getChannel().block());
      this.currentGuild = g;
    }

    @CommandLine.Command(name = "ban", description = "Bans users")
    void ban(
        @CommandLine.Option(
            names = {"-r", "--reason"},
            defaultValue = "No reason given",
            description = "Ban reason. Defaults to '${DEFAULT-VALUE}'"
        )
            String reason,
        @CommandLine.Option(
            names = {"-d", "--delete"},
            defaultValue = "0",
            description = "Delete messages from previous <int> days. Defaults to '${DEFAULT-VALUE}'"
        )
            int deleteMessages,
        @CommandLine.Parameters(
            arity = "1..*",
            paramLabel = "<@mentions>",
            description = "Member @mention(s) to be banned"
        )
            DisUtil.CLIMember[] mentionObjects
    ) {
      DisUtil.SendMessage(event, requestingMember.reduce(
          e -> "Requesting Member not found, are we in private?",
          rm -> {
            if (!DisUtil.VerifyPermission(rm, Permission.BAN_MEMBERS)) {
              return DisUtil.MentionUser(rm) + " Missing permission 'BAN_MEMBERS'";
            }
            return DisUtil.MentionUser(rm) + " Ban Status:\n"
                + "Reason: " + reason + "\n"
                + "Deleting member message(s) from previous " + deleteMessages + " day(s).\n"
                + DisUtil.ForEachMember(
                (Member m) -> Exceptional.eitherOfExceptional(
                    () -> {
                      DisUtil.OneTimeInvite invite = DisUtil.OneTimeInvite.Gen(currentGuild.fromRight(null));
                      String dmStatus = DisUtil.SendPrivate(m,
                          "You have been banned from: " + currentGuild.fromRight(null).getName() + "\n"
                          + "Reason: " + reason + "\n"
                          + "Your message(s) from previous " + deleteMessages + " day(s) have been deleted.\n"
                          + "Invite link: " + invite + "\n"
                          + ""
                      ).reduce(
                          e -> {
                            invite.DeleteInvite();
                            return "Pardon DM Failure - " + DisUtil.ExceptionParser(e);
                          },
                          p -> " - Pardon DM sent"
                      );
                      return Exceptional.eitherOfExceptionalV(() -> {
                        m.ban(
                            (BanQuerySpec spec) -> {
                              spec.setReason(reason);
                              spec.setDeleteMessageDays(deleteMessages);
                            }
                        ).block();
                      }).reduceL(
                          e -> "Exception - " + DisUtil.ExceptionParser(e),
                          () -> "BANNED" + dmStatus
                      );
                    }
                ),
                mentionObjects);
                }
            )
      );
    }

    @CommandLine.Command(name = "pardon", description = "Pardons banned users")
    void pardon(
        @CommandLine.Option(
            names = {"-r", "--reason"},
            defaultValue = "No reason given",
            description = "Ban reason. Defaults to '${DEFAULT-VALUE}'"
        )
            String reason,
        @CommandLine.Parameters(
            arity = "1..*",
            paramLabel = "<@mentions>",
            description = "User @mention(s) to be pardoned"
        )
            DisUtil.CLIUser[] mentionObjects
    ) {
      Member requestingUser = event.getMember().get();
      if (!DisUtil.VerifyPermission(requestingUser, Permission.BAN_MEMBERS)) {
        DisUtil.SendMessage(event, DisUtil.MentionUser(requestingUser) + " Missing permission 'BAN_MEMBERS'");
        return;
      }
      DisUtil.SendMessage(event, DisUtil.MentionUser(requestingUser) + " Pardon Status:\n"
          + "Reason: " + reason + "\n"
          + DisUtil.ForEachUser(
          (User u) -> Exceptional.eitherOfExceptional(
              () -> currentGuild.map(
                  g -> Exceptional.eitherOfExceptional(() -> {
                      g.unban(u.getId(), reason).block();
                      return true;
                    }).reduce(
                        e -> "Failure - " + DisUtil.ExceptionParser(e),
                        x -> {
                          DisUtil.OneTimeInvite invite = DisUtil.OneTimeInvite.Gen(currentGuild.fromRight(null));
                          return  "Pardoned" + DisUtil.SendPrivate(u, "You have been pardoned from: " + g.getName() + "\n"
                              + "Reason: " + reason + "\n"
                              + "Invite link: " + invite).reduce(
                              e -> {
                                invite.DeleteInvite();
                                return " - DM failure - " + DisUtil.ExceptionParser(e);
                              },
                              m -> " - DM sent, I've see him elsewhere..."
                          );
                        })
              ).reduce(
                  e -> "Exception: " + DisUtil.ExceptionParser(e),
                  m -> m
              )
          ),
          mentionObjects));
      }

    @CommandLine.Command(name = "kick", description = "Kicks users")
    void kick(
        @CommandLine.Option(
            names = {"-r", "--reason"},
            defaultValue = "No reason given",
            description = "Kick reason. Defaults to '${DEFAULT-VALUE}'"
        )
            String reason,
        @CommandLine.Parameters(
            arity = "1..*",
            paramLabel = "<@mentions>",
            description = "Member @mention(s) to be kicked"
        )
            DisUtil.CLIMember[] mentionObjects
    ) {
      Member requestingUser = event.getMember().get();
      if (!DisUtil.VerifyPermission(requestingUser, Permission.KICK_MEMBERS)) {
        DisUtil.SendMessage(event, DisUtil.MentionUser(requestingUser) + " Missing permission 'KICK_MEMBERS'");
        return;
      }
      DisUtil.SendMessage(event, DisUtil.MentionUser(requestingUser) + " Status:\n" + DisUtil.ForEachMember(
        (Member m) ->
          Exceptional.eitherOfExceptional(
              () -> {
                m.kick(reason).block();
                return "KICKED";
              }
          ),
        mentionObjects
        )
      );
    }

    @Override
    public void run() {
      DisUtil.SendMessage(event, new CommandLine.ParameterException(spec.commandLine(), "Specify a command").getMessage());
    }
  }

  @EventHandler(priority = 1)
  public static EventHandler.Action Handler(MessageCreateEvent e) {
    String messageString = e.getMessage().getContent();
    if (messageString.startsWith(commandPrefix)) {
      CommandLine cmd = DisUtil.GetGuild(e).reduce(
          ex -> new CommandLine(new MessageCommand(e, Either.left(ex))),
          g -> {
            CommandLine ret = new CommandLine(new MessageCommand(e, Either.right(g)));
            ret.registerConverter(DisUtil.CLIMember.class, DisUtil.CLIMember.CLIMemberFromMention(e.getClient(), g.getId()));
            return ret;
          }
      );
      cmd.registerConverter(DisUtil.CLIUser.class, DisUtil.CLIUser.CLIUserFromMention(e.getClient()));
      // 'registerConverter(java.lang.Class<K>, picocli.CommandLine.ITypeConverter<K>)'
      // in 'picocli.CommandLine' cannot be applied to
      // '(java.lang.Class<com.lostofthought.lostbot.DisUtil.CLIUser>, picocli.CommandLine.ITypeConverter<java.lang.Object>)'
      ChatWriter cw = new ChatWriter(e);
      cmd.setOut(cw);
      cmd.setErr(cw);
      cmd.getCommandSpec().mixinStandardHelpOptions(true);
      String[] margs = CLI.SplitIntoArgs(messageString.substring(commandPrefix.length()));
      if (margs.length == 0) {
        cw.print(cmd.getUsageMessage());
      } else {
        cmd.execute(margs);
      }
      return EventHandler.Action.Break;
    }
    return EventHandler.Action.Continue;
  }
}
