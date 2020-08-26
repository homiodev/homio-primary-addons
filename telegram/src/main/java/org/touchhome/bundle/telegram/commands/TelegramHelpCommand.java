package org.touchhome.bundle.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Log4j2
public final class TelegramHelpCommand extends TelegramBaseCommand {

    private final ICommandRegistry mCommandRegistry;

    public TelegramHelpCommand(ICommandRegistry mCommandRegistry) {
        super("help", "List all known commands");
        this.mCommandRegistry = mCommandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        sb.append("<b>Available commands:</b>\n");
        mCommandRegistry.getRegisteredCommands().forEach(cmd -> sb.append(cmd.toString()));
        message.enableHtml(true);
    }
}
