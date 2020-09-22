package org.touchhome.bundle.http.commands;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Log4j2
public final class TelegramStartCommand extends TelegramBaseCommand {

    private final ICommandRegistry mCommandRegistry;

    public TelegramStartCommand(ICommandRegistry mCommandRegistry) {
        super("start", "");
        this.mCommandRegistry = mCommandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        TelegramHelpCommand.assembleCommands(mCommandRegistry, sb, message);
    }
}
