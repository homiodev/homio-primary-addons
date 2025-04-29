package org.homio.addon.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.homio.addon.telegram.TelegramEntity.TelegramUser;
import org.homio.api.Context;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static org.homio.addon.telegram.service.TelegramService.TelegramBot;

@Log4j2
public final class TelegramUnregisterUserCommand extends TelegramBaseCommand {

    public static final String UNREGISTER_COMMAND = "unregister";
    private final Context context;

    public TelegramUnregisterUserCommand(Context context, TelegramBot telegramBot) {
        super(UNREGISTER_COMMAND, "Unregister user", telegramBot);
        this.context = context;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        TelegramUser entity = telegramBot.getTelegramEntity().getUser(user.getId());
        if (entity != null) {
            telegramBot.getTelegramEntity().removeUser(user.getId());
            context.db().save(telegramBot.getTelegramEntity());
            sb.append("User: <").append(entity.getName()).append("> successfully removed");
            log.info("Telegram user <{}> successfully removed", entity.getName());
        } else {
            sb.append("User <").append(user.getFirstName()).append("> not registered.");
            log.info("Telegram user <{}> not registered for removing.", user.getFirstName());
        }
    }
}
