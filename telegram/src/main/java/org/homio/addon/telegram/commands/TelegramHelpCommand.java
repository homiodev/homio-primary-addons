package org.homio.addon.telegram.commands;

import lombok.extern.log4j.Log4j2;
import org.homio.addon.telegram.TelegramEntity;
import org.homio.addon.telegram.service.TelegramService;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static org.homio.addon.telegram.commands.TelegramRegisterUserCommand.REGISTER_COMMAND;
import static org.homio.addon.telegram.commands.TelegramUnregisterUserCommand.UNREGISTER_COMMAND;

@Log4j2
public final class TelegramHelpCommand extends TelegramBaseCommand {

    public TelegramHelpCommand(TelegramService.TelegramBot telegramBot) {
        super("help", "List all known commands", telegramBot);
    }

    static void assembleCommands(ICommandRegistry mCommandRegistry, StringBuilder sb,
                                 SendMessage message, boolean userUnregistered) {
        sb.append("<b>Available commands:</b>\n");
        mCommandRegistry.getRegisteredCommands().forEach(cmd -> {
            if (cmd.getCommandIdentifier().equals(REGISTER_COMMAND) && !userUnregistered) {
                return;
            }
            if (cmd.getCommandIdentifier().equals(UNREGISTER_COMMAND) && userUnregistered) {
                return;
            }
            if (!cmd.getCommandIdentifier().equals("start")) {
                sb.append(cmd);
            }
        });
        sb.append("More commands may be added: <a href=\"https://homio.org/client/workspace\">homio.org</a>");
        message.enableHtml(true);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings, StringBuilder sb, SendMessage message) {
        TelegramEntity.TelegramUser entity = telegramBot.getTelegramEntity().getUser(user.getId());
        assembleCommands(telegramBot, sb, message, entity == null);
    }
}
