package org.homio.addon.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.homio.addon.telegram.TelegramEntity.TelegramUser;
import org.homio.addon.telegram.service.TelegramService.TelegramBot;
import org.homio.api.EntityContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Log4j2
public final class TelegramUnregisterUserCommand extends TelegramBaseCommand {

  private final EntityContext entityContext;

    public TelegramUnregisterUserCommand(EntityContext entityContext, TelegramBot telegramBot) {
        super("unregister", "Unregister user", telegramBot);
        this.entityContext = entityContext;
    }

  @Override
  public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
      TelegramUser entity = telegramBot.getTelegramEntity().getUser(user.getId());
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
