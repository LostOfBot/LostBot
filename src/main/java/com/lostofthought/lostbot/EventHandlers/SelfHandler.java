package com.lostofthought.lostbot.EventHandlers;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;

public class SelfHandler {
  public static User Self;
  @EventHandler(priority = 100)
  public static EventHandler.Action Handler(ReadyEvent e){
    Self = e.getSelf();
    //ChatWriter cw = new ChatWriter(e);
    //cw.print("Hello? Anyone there? Hmm... Maybe they are somewhere else...");
    System.out.printf("Logged in as %s#%s%n", Self.getUsername(), Self.getDiscriminator());
    return EventHandler.Action.RemoveAndBreak;
  }
}
