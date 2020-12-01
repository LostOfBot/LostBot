package com.lostofthought.lostbot;

import com.lostofthought.util.OutputStreamUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;

import java.io.PrintWriter;

public class ChatWriter extends PrintWriter {

  MessageCreateEvent event;

  public ChatWriter(MessageCreateEvent event) {
    super(new OutputStreamUtil.NullOutputStream());
    this.event = event;
  }

  @Override
  public void print(Object object) {
    this.print(object.toString());
  }

  @Override
  public void print(String string) {
    final MessageChannel channel = event.getMessage().getChannel().block();
    assert channel != null;
    channel.createMessage(string).block();
  }

  @Override
  public void println(String string) {
    this.print(string);
  }

}
