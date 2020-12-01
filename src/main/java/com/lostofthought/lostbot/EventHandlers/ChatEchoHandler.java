package com.lostofthought.lostbot.EventHandlers;

import com.lostofthought.lostbot.ChatWriter;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class ChatEchoHandler {
  @EventHandler(priority = -1, disabled = true)
  public static EventHandler.Action Handler(MessageCreateEvent e){
    String messageString = e.getMessage().getContent();
    new ChatWriter(e).print(messageString + "... Huh?");
    return EventHandler.Action.Continue;
  }
}
