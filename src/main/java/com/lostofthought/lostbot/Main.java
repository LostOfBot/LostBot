package com.lostofthought.lostbot;

import com.lostofthought.lostbot.EventHandlers.EventHandler;
import com.lostofthought.util.*;
import com.lostofthought.util.PriorityList;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.*;
import discord4j.core.event.domain.channel.*;
import discord4j.core.event.domain.guild.*;
import discord4j.core.event.domain.lifecycle.*;
import discord4j.core.event.domain.message.*;
import discord4j.core.event.domain.role.RoleCreateEvent;
import discord4j.core.event.domain.role.RoleDeleteEvent;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import discord4j.core.object.entity.User;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class Main {
  public static DiscordClient client;
  public static GatewayDiscordClient gateway;
  public static User self;

  @SuppressWarnings("unchecked")
  public static void main(String[] args){
    final String token;
    try {
      token = new String(Files.readAllBytes(Paths.get("DISCORD_TOKEN")), StandardCharsets.UTF_8).trim();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(""
          + "Failed to get discord token\n"
          + "Exiting..."
      );
      return;
    }
    Map<Class<? extends Event>, PriorityList<Function<? extends Event, EventHandler.Action>>> eventHandlers = new HashMap<>();
    Reflections reflections = new Reflections(
        "com.lostofthought",
        new MethodAnnotationsScanner());
    reflections.getMethodsAnnotatedWith(com.lostofthought.lostbot.EventHandlers.EventHandler.class).forEach(
        handler -> {
          EventHandler annotation = handler.getAnnotation(EventHandler.class);
          if(annotation.disabled()){
            return;
          }
          PriorityList<Function<? extends Event, EventHandler.Action>> priorityList
              = eventHandlers.computeIfAbsent((Class<? extends Event>) handler.getParameterTypes()[0], k -> new PriorityList<>());
          priorityList.add(
              annotation.priority(),
              (Event event) -> {
                try {
                  return (EventHandler.Action) handler.invoke(null, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                  e.printStackTrace();
                  return EventHandler.Action.Continue;
                }
              });
        }
    );

    final DiscordClient client = DiscordClient.create(token);
    final GatewayDiscordClient gateway = client.login().block();
    if(gateway == null){
      return;
    }
    gateway.on(Event.class).flatMap((Event e) -> {
      try {
        System.out.println(e);
        if(eventHandlers.containsKey(e.getClass())){
          for (Function<? extends Event, EventHandler.Action> f : eventHandlers.get(e.getClass())) {
            EventHandler.Action a = f.apply(Cast.unchecked(e));
            if(a == EventHandler.Action.RemoveAndContinue || a == EventHandler.Action.RemoveAndBreak){
              PriorityList<?> list = eventHandlers.get(e.getClass());
              if(list.size() <= 0){
                eventHandlers.remove(e.getClass());
              }
            }
            if(a == EventHandler.Action.Break || a == EventHandler.Action.RemoveAndBreak){
              break;
            }
          }
        }
      } catch (Exception ex) {
        System.out.println("Exception: " + ex);
      }
      return Mono.empty();
      }).subscribe();
    gateway.onDisconnect().block();
  }
}