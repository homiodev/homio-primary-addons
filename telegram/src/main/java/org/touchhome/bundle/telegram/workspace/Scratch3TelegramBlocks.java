package org.touchhome.bundle.telegram.workspace;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.UserEntity;
import org.touchhome.bundle.api.scratch.*;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.telegram.TelegramEntrypoint;
import org.touchhome.bundle.telegram.service.TelegramService;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class Scratch3TelegramBlocks extends Scratch3ExtensionBlocks {
    public static final String URL = "rest/v2/telegram/";

    private static final String USER = "USER";
    private static final String MESSAGE = "MESSAGE";
    public static final String COMMAND = "COMMAND";
    public static final String DESCRIPTION = "DESCRIPTION";
    private static final String LEVEL = "LEVEL";

    private final TelegramService telegramService;

    private final MenuBlock.StaticMenuBlock levelMenu;
    private final MenuBlock.ServerMenuBlock telegramUsersMenu;
    private final Scratch3Block sendMessageCommand;
    private final Scratch3Block getCommand;
    private final BroadcastLockManager broadcastLockManager;

    public Scratch3TelegramBlocks(TelegramService telegramService, EntityContext entityContext,
                                  BroadcastLockManager broadcastLockManager,
                                  TelegramEntrypoint telegramEntrypoint) {
        super("telegram", "#73868c", entityContext, telegramEntrypoint);
        this.telegramService = telegramService;
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.telegramUsersMenu = MenuBlock.ofServer("telegramUsersMenu", URL + "user/options", "All", "all");
        this.levelMenu = MenuBlock.ofStatic("levelMenu", Level.class);

        this.getCommand = Scratch3Block.ofHandler(10, "get_msg", BlockType.hat, "On command [COMMAND] (description) [DESCRIPTION]", this::whenGetMessage);
        this.getCommand.addArgument(COMMAND, ArgumentType.string);
        this.getCommand.addArgument(DESCRIPTION, ArgumentType.string);

        this.sendMessageCommand = Scratch3Block.ofHandler(20, "send_msg", BlockType.command, "Send [MESSAGE] to user [USER]. [LEVEL]", this::sendMessageCommand);
        this.sendMessageCommand.addArgument(MESSAGE, ArgumentType.string);
        this.sendMessageCommand.addArgumentServerSelection(USER, this.telegramUsersMenu);
        this.sendMessageCommand.addArgument(LEVEL, ArgumentType.string, Level.info, this.levelMenu);

        this.postConstruct();
    }

    private void whenGetMessage(WorkspaceBlock workspaceBlock) {
        WorkspaceBlock substack = workspaceBlock.getNext();
        if (substack == null) {
            workspaceBlock.logErrorAndThrow("No next block found");
            return;
        }
        String command = workspaceBlock.getInputString(COMMAND);
        String description = workspaceBlock.getInputString(DESCRIPTION);
        BroadcastLock lock = broadcastLockManager.getOrCreateLock(workspaceBlock.getId());
        this.telegramService.getTelegramBot().registerEvent(command, description, workspaceBlock.getId(), lock);

        while (!Thread.currentThread().isInterrupted()) {
            if (lock.await(workspaceBlock)) {
                substack.handle();
            }
        }
    }

    private void sendMessageCommand(WorkspaceBlock workspaceBlock) {
        String user = workspaceBlock.getMenuValue(USER, this.telegramUsersMenu, String.class);
        Level level = workspaceBlock.getMenuValue(LEVEL, this.levelMenu, Level.class);
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
        telegramService.sendMessage(users, level.format(message));
    }

    private enum Level {
        info, warn, error;

        public String format(String message) {
            return "[" + name().toUpperCase() + "]. " + message;
        }
    }
}
