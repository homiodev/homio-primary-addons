package org.touchhome.bundle.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.util.Constants;

import java.util.Collections;

@Log4j2
public final class TelegramRegisterUserCommand extends TelegramBaseCommand {

    private final EntityContext entityContext;

    public TelegramRegisterUserCommand(EntityContext entityContext) {
        super("register", "Register user");
        this.entityContext = entityContext;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        UserEntity entity = entityContext.getEntity(UserEntity.PREFIX + user.getId());
        if (entity != null) {
            sb.append("User: <").append(entity.getName()).append("> already registered");
            log.info("Telegram user <{}> already registered", entity.getName());
        } else {
            entity = new UserEntity()
                    .setName(user.getFirstName())
                    .setUserType(UserEntity.UserType.OTHER)
                    .setUserId(String.valueOf(user.getId()))
                    .setJsonData(new JSONObject().put(CHAT_ID, String.valueOf(chat.getId())))
                    .setRoles(Collections.singleton(Constants.GUEST_ROLE))
                    .setEntityID(UserEntity.PREFIX + user.getId());
            entityContext.save(entity);
            sb.append("User <").append(entity.getName()).append("> has been registered successfully.");
            log.info("Telegram user <{}> has been registered successfully.", entity.getName());
        }
    }
}
