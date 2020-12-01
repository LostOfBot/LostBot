package com.lostofthought.lostbot.EventHandlers;

import discord4j.core.event.domain.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventHandler {
  enum Action {
    Continue,
    Break,
    RemoveAndContinue,
    RemoveAndBreak
  }
  int priority() default 0;
  boolean disabled() default false;
  abstract class Func <E extends Event> implements Function<E, EventHandler.Action>{} // TODO:
}
