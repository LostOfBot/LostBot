package com.lostofthought.lostbot.EventHandlers;

import com.lostofthought.lostbot.ChatWriter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;

import java.util.Optional;

public class DropSelfChatHandler {
  @EventHandler(priority = 10)
  public static EventHandler.Action Handler(MessageCreateEvent e){
    Optional<Member> u = e.getMember();
    if(u.isPresent() && u.get().getId().equals(SelfHandler.Self.getId())){
      return EventHandler.Action.Break;
    }
    return EventHandler.Action.Continue;
  }
}
