package org.touchhome.bundle.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.telegram.TelegramEntity;
import org.touchhome.bundle.telegram.service.TelegramService;

@Log4j2
public final class TelegramUnregisterUserCommand extends TelegramBaseCommand {

  private final EntityContext entityContext;

  public TelegramUnregisterUserCommand(EntityContext entityContext, TelegramService.TelegramBot telegramBot) {
    super("unregister", "Unregister user", telegramBot);
    this.entityContext = entityContext;
  }

  @Override
  public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
    TelegramEntity.TelegramUser entity = telegramBot.getTelegramEntity().getUser(user.getId());
    if (entity != null) {
      telegramBot.getTelegramEntity().removeUser(user.getId());
      entityContext.save(telegramBot.getTelegramEntity());
      sb.append("User: <").append(entity.getName()).append("> successfully removed");
      log.info("Telegram user <{}> successfully removed", entity.getName());
    } else {
      sb.append("User <").append(user.getFirstName()).append("> not registered.");
      log.info("Telegram user <{}> not registered for removing.", user.getFirstName());
    }
  }
}
