package org.homio.addon.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.telegram.TelegramEntity.TelegramUser;
import org.homio.api.workspace.Lock;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.homio.addon.telegram.service.TelegramService.TelegramBot;

@Log4j2
public final class TelegramEventCommand extends TelegramBaseCommand {

    private final Map<String, Lock> handlers = new HashMap<>();

    public TelegramEventCommand(String commandIdentifier, String description, TelegramBot telegramBot) {
        super(commandIdentifier, "Event: '" + StringUtils.defaultIfBlank(description, commandIdentifier) + "'",
                telegramBot);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        TelegramUser telegramUser = telegramBot.getTelegramEntity().getUser(user.getId());
        if (telegramUser == null) {
            sb.append("User: <").append(user.getFirstName()).append("> not registered, please register user first");
        } else {
            for (Lock lock : handlers.values()) {
                lock.signalAll(strings.length == 0 ? "" : strings.length == 1 ? strings[0] : Arrays.asList(strings));
            }
        }
    }

    public void addHandler(String workspaceId, Lock lock) {
        handlers.put(workspaceId, lock);
    }

    public void removeHandler(String workspaceId) {
        handlers.remove(workspaceId);
    }

    public boolean hasHandlers() {
        return !handlers.isEmpty();
    }
}
