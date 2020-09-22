package org.touchhome.bundle.http.commands;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.UserEntity;
import org.touchhome.bundle.api.workspace.BroadcastLock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public final class TelegramEventCommand extends TelegramBaseCommand {

    private final Map<String, BroadcastLock> handlers = new HashMap<>();
    private final EntityContext entityContext;

    public TelegramEventCommand(String commandIdentifier, String description, EntityContext entityContext) {
        super(commandIdentifier, "Event: '" + StringUtils.defaultIfBlank(description, commandIdentifier) + "'");
        this.entityContext = entityContext;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        UserEntity entity = entityContext.getEntity(UserEntity.PREFIX + user.getId());
        if (entity == null) {
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
