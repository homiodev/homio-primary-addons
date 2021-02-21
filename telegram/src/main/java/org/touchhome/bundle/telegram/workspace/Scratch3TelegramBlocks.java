package org.touchhome.bundle.telegram.workspace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.*;
import org.touchhome.bundle.telegram.TelegramEntryPoint;
import org.touchhome.bundle.telegram.service.TelegramService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Component
public class Scratch3TelegramBlocks extends Scratch3ExtensionBlocks {
    public static final String URL = "rest/telegram/";
    public static final String COMMAND = "COMMAND";
    public static final String DESCRIPTION = "DESCRIPTION";
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[\\{\\}\\.\\+\\-\\#\\(\\)]");
    private static final String USER = "USER";
    private static final String MESSAGE = "MESSAGE";
    private static final String LEVEL = "LEVEL";

    private final TelegramService telegramService;

    private final MenuBlock.StaticMenuBlock<Level> levelMenu;
    private final MenuBlock.ServerMenuBlock telegramUsersMenu;
    private final Scratch3Block sendMessageCommand;
    private final Scratch3Block getCommand;
    private final BroadcastLockManager broadcastLockManager;

    public Scratch3TelegramBlocks(TelegramService telegramService, EntityContext entityContext,
                                  BroadcastLockManager broadcastLockManager,
                                  TelegramEntryPoint telegramEntryPoint) {
        super("#73868c", entityContext, telegramEntryPoint);
        this.telegramService = telegramService;
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.telegramUsersMenu = MenuBlock.ofServer("telegramUsersMenu", URL + "user/options", "All", "all");
        this.levelMenu = MenuBlock.ofStatic("levelMenu", Level.class, Level.info);

        this.getCommand = Scratch3Block.ofHandler(10, "get_msg", BlockType.hat, "On command [COMMAND] (description) [DESCRIPTION]", this::whenGetMessage);
        this.getCommand.addArgument(COMMAND, ArgumentType.string);
        this.getCommand.addArgument(DESCRIPTION, ArgumentType.string);

        this.sendMessageCommand = Scratch3Block.ofHandler(20, "send_msg", BlockType.command, "Send [MESSAGE] to user [USER]. [LEVEL]", this::sendMessageCommand);
        this.sendMessageCommand.addArgument(MESSAGE, ArgumentType.string);
        this.sendMessageCommand.addArgument(USER, this.telegramUsersMenu);
        this.sendMessageCommand.addArgument(LEVEL, this.levelMenu);
    }

    private static String escape(String text) {
        Matcher matcher = ESCAPE_PATTERN.matcher(text);
        StringBuffer noteBuffer = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            matcher.appendReplacement(noteBuffer, "\\\\" + group);
        }
        matcher.appendTail(noteBuffer);
        return noteBuffer.length() == 0 ? text : noteBuffer.toString();
    }

    private void whenGetMessage(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getNextOrThrow();
        String command = workspaceBlock.getInputString(COMMAND);
        String description = workspaceBlock.getInputString(DESCRIPTION);
        BroadcastLock lock = broadcastLockManager.getOrCreateLock(workspaceBlock);
        this.telegramService.getTelegramBot().registerEvent(command, description, workspaceBlock.getId(), lock);

        workspaceBlock.subscribeToLock(lock);
    }

    private void sendMessageCommand(WorkspaceBlock workspaceBlock) {
        String user = workspaceBlock.getMenuValue(USER, this.telegramUsersMenu);
        Level level = workspaceBlock.getMenuValue(LEVEL, this.levelMenu);
        String message = workspaceBlock.getInputString(MESSAGE);

        List<UserEntity> users;
        if ("all".equals(user)) {
            users = this.telegramService.getUsers();
            if (users.isEmpty()) {
                workspaceBlock.logWarn("Unable to find any registered users. Please open telegram bot and run command '/register'");
            }
        } else {
            users = new ArrayList<>();
            UserEntity userEntity = this.entityContext.getEntity(user);
            if (userEntity == null) {
                workspaceBlock.logError("Unable to find user with id <{}>", user);
            } else {
                users.add(userEntity);
            }
        }
        for (UserEntity userEntity : users) {
            workspaceBlock.logInfo("Send event to telegram user <{}>. Message <{}>", userEntity.getName(), message);
        }
        try {
            try {
                new JSONObject(message);
                // wrap json into code block
                message = "```" + message + "```";
            } catch (Exception ignore) {
            }
            telegramService.sendMessage(users, level.format(escape(message)));
        } catch (TelegramApiException ex) {
            workspaceBlock.logError("Error send telegram message " + ex);
        }
    }

    @RequiredArgsConstructor
    private enum Level {
        none(new byte[0]), info(new byte[]{-30, -124, -71, -17, -72, -113}), warn(new byte[]{-30, -102, -96, -17, -72, -113}), error(new byte[]{-16, -97, -122, -104});

        private final byte[] emoji;

        public String format(String message) {
            if (this.emoji.length > 0) {
                return new String(this.emoji) + " " + message;
            }
            return message;
        }
    }
}
