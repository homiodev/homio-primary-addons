package org.touchhome.bundle.telegram.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.telegram.TelegramEntity;
import org.touchhome.bundle.telegram.service.TelegramService;

@Log4j2
public final class TelegramEventCommand extends TelegramBaseCommand {

  private final Map<String, BroadcastLock> handlers = new HashMap<>();

  public TelegramEventCommand(String commandIdentifier, String description, TelegramService.TelegramBot telegramBot) {
    super(commandIdentifier, "Event: '" + StringUtils.defaultIfBlank(description, commandIdentifier) + "'",
        telegramBot);
  }

  @Override
  public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
    TelegramEntity.TelegramUser telegramUser = telegramBot.getTelegramEntity().getUser(user.getId());
    if (telegramUser == null) {
      sb.append("User: <").append(user.getFirstName()).append("> not registered, please register user first");
    } else {
      for (BroadcastLock broadcastLock : handlers.values()) {
        broadcastLock.signalAll(strings.length == 0 ? "" : strings.length == 1 ? strings[0] : Arrays.asList(strings));
      }
    }
  }

  public void addHandler(String workspaceId, BroadcastLock lock) {
    handlers.put(workspaceId, lock);
  }

  public void removeHandler(String workspaceId) {
    handlers.remove(workspaceId);
  }

  public boolean hasHandlers() {
    return !handlers.isEmpty();
  }
}
