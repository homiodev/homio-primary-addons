package org.touchhome.bundle.http.commands;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.UserEntity;

@Log4j2
public final class TelegramUnregisterUserCommand extends TelegramBaseCommand {

    private final EntityContext entityContext;

    public TelegramUnregisterUserCommand(EntityContext entityContext) {
        super("unregister", "Unregister user");
        this.entityContext = entityContext;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        UserEntity entity = entityContext.getEntity(UserEntity.PREFIX + user.getId());
        if (entity != null) {
            if (!entity.getJsonData().has(CHAT_ID)) {
                sb.append("Unable to remove user: <").append(entity.getName()).append(">");
            } else {
                entityContext.delete(entity);
                sb.append("User: <").append(entity.getName()).append("> successfully removed");
                log.info("Telegram user <{}> successfully removed", entity.getName());
            }
        } else {
            sb.append("User <").append(user.getFirstName()).append("> not registered.");
            log.info("Telegram user <{}> not registered for removing.", user.getFirstName());
        }
    }
}
