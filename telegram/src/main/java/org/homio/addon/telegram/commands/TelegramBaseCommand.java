package org.homio.addon.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.homio.addon.telegram.service.TelegramService;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
public abstract class TelegramBaseCommand extends BotCommand {

    public static final String CHAT_ID = "TELEGRAM_CHAT_ID";
    protected final TelegramService.TelegramBot telegramBot;

    TelegramBaseCommand(String commandIdentifier, String description, TelegramService.TelegramBot telegramBot) {
        super(commandIdentifier, description + "\n");
        this.telegramBot = telegramBot;
    }

    @Override
    public String toString() {
        return "<b>/" + this.getCommandIdentifier() + "</b> - " + this.getDescription();
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException ex) {
            log.error("Error: <{}>", ex.toString(), ex);
        }
    }

    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        StringBuilder sb = new StringBuilder();

        SendMessage message = new SendMessage();
        this.execute(absSender, user, chat, strings, sb, message);

        if (sb.length() > 0) {
            message.setChatId(chat.getId().toString());
            message.setText(sb.toString());
            execute(absSender, message, user);
        }
    }

    public abstract void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message);
}
