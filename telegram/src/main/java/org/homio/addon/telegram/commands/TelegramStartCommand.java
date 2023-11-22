package org.homio.addon.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.homio.addon.telegram.service.TelegramService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Log4j2
public final class TelegramStartCommand extends TelegramBaseCommand {

    public TelegramStartCommand(TelegramService.TelegramBot telegramBot) {
        super("start", "", telegramBot);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        TelegramHelpCommand.assembleCommands(telegramBot, sb, message);
    }
}
