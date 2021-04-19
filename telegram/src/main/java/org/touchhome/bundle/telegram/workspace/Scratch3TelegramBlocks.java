package org.touchhome.bundle.telegram.workspace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.state.RawType;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.api.util.Curl;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.*;
import org.touchhome.bundle.telegram.TelegramEntryPoint;
import org.touchhome.bundle.telegram.service.TelegramService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Component
public class Scratch3TelegramBlocks extends Scratch3ExtensionBlocks {
    public static final Set<String> PHOTO_EXTENSIONS = new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".jpe", ".jif", ".jfif",
            ".jfi", ".webp"));

    public static final String URL = "rest/telegram/";
    public static final String COMMAND = "COMMAND";
    public static final String DESCRIPTION = "DESCRIPTION";
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[\\{\\}\\.\\+\\-\\#\\(\\)]");
    private static final String USER = "USER";
    private static final String MESSAGE = "MESSAGE";
    private static final String LEVEL = "LEVEL";

    private final TelegramService telegramService;

    private final MenuBlock.StaticMenuBlock<Level> levelMenu;
    private final MenuBlock.StaticMenuBlock<ByteType> byteType;
    private final MenuBlock.ServerMenuBlock telegramUsersMenu;

    private final Scratch3Block sendMessageCommand;
    private final Scratch3Block getCommand;
    private final BroadcastLockManager broadcastLockManager;
    private final Scratch3Block sendQuestionMessageCommand;
    private final Scratch3Block sendImageMessageCommand;

    public Scratch3TelegramBlocks(TelegramService telegramService, EntityContext entityContext,
                                  BroadcastLockManager broadcastLockManager,
                                  TelegramEntryPoint telegramEntryPoint) {
        super("#73868c", entityContext, telegramEntryPoint);
        this.telegramService = telegramService;
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.telegramUsersMenu = MenuBlock.ofServer("telegramUsersMenu", URL + "user/options", "All", "all");
        this.levelMenu = MenuBlock.ofStatic("levelMenu", Level.class, Level.info);
        this.byteType = MenuBlock.ofStatic("type", ByteType.class, ByteType.Image);


        this.getCommand = Scratch3Block.ofHandler(10, "get_msg", BlockType.hat, "On command [COMMAND] (description) [DESCRIPTION]", this::whenGetMessage);
        this.getCommand.addArgument(COMMAND, ArgumentType.string);
        this.getCommand.addArgument(DESCRIPTION, ArgumentType.string);

        this.sendMessageCommand = Scratch3Block.ofHandler(20, "send_msg", BlockType.command,
                "Send [MESSAGE] to user [USER]. [LEVEL]", this::sendMessageCommand);
        this.sendMessageCommand.addArgument(MESSAGE, ArgumentType.string);
        this.sendMessageCommand.addArgument(USER, this.telegramUsersMenu);
        this.sendMessageCommand.addArgument(LEVEL, this.levelMenu);

        this.sendQuestionMessageCommand = Scratch3Block.ofHandler(30, "send_question", BlockType.command,
                "Ask [MESSAGE] to user [USER] | Buttons: [BUTTONS]", this::sendQuestionMessageCommand);
        this.sendQuestionMessageCommand.addArgument(MESSAGE, ArgumentType.string);
        this.sendQuestionMessageCommand.addArgument(USER, this.telegramUsersMenu);
        this.sendQuestionMessageCommand.addArgument("BUTTONS", "YES,NO");

        this.sendImageMessageCommand = Scratch3Block.ofHandler(30, "send_img", BlockType.command,
                "Send [TYPE] [MESSAGE], caption [CAPTION] to user [USER]", this::sendIVAMessageCommand);
        this.sendImageMessageCommand.addArgument("TYPE", this.byteType);
        this.sendImageMessageCommand.addArgument(MESSAGE, ArgumentType.string);
        this.sendImageMessageCommand.addArgument(USER, this.telegramUsersMenu);
        this.sendImageMessageCommand.addArgument("CAPTION", "caption");
    }

    private void sendIVAMessageCommand(WorkspaceBlock workspaceBlock) {
        ByteType byteType = workspaceBlock.getMenuValue("TYPE", this.byteType);
        Object image = workspaceBlock.getInput(MESSAGE, true);
        String caption = workspaceBlock.getInputString("CAPTION");
        InputFile inputFile = null;
        if (image instanceof RawType) {
            inputFile = new InputFile(new ByteArrayInputStream(((RawType) image).byteArrayValue()), ((RawType) image).getName());
        }
        if (image instanceof String || image instanceof StringType) {
            inputFile = createInputFileFromString(image.toString(), byteType);
        }

        try {
            if (ByteType.Image.equals(byteType)) {
                telegramService.sendPhoto(getUsers(workspaceBlock), inputFile, caption);
            } else {
                telegramService.sendVideo(getUsers(workspaceBlock), inputFile, caption);
            }
        } catch (Exception ex) {
            workspaceBlock.logError("Error send telegram message " + ex);
        }
    }

    @SneakyThrows
    private InputFile createInputFileFromString(String photoURL, ByteType byteType) {
        if (photoURL.startsWith("http")) {
            // max 10mb
            return new InputFile(new ByteArrayInputStream(Curl.download(photoURL, byteType.maxLength)), photoURL);
        } else if (photoURL.startsWith("file:") || PHOTO_EXTENSIONS.stream().anyMatch(photoURL::endsWith)) {
            String temp = photoURL;
            if (!photoURL.startsWith("file:")) {
                temp = "file://" + photoURL;
            }
            return new InputFile(Paths.get(new java.net.URL(temp).getPath()).toFile(), photoURL);
        } else {
            final String photoB64Data;
            if (photoURL.startsWith("data:")) { // support data URI scheme
                String[] photoURLParts = photoURL.split(",");
                if (photoURLParts.length > 1) {
                    photoB64Data = photoURLParts[1];
                } else {
                    return null;
                }
            } else {
                photoB64Data = photoURL;
            }
            InputStream is = Base64.getDecoder().wrap(new ByteArrayInputStream(photoB64Data.getBytes(StandardCharsets.UTF_8)));
            return new InputFile(is, "base64");
        }
    }

    private void sendQuestionMessageCommand(WorkspaceBlock workspaceBlock) {
        String message = workspaceBlock.getInputString(MESSAGE);
        String[] buttons = workspaceBlock.getInputString("BUTTONS").split(",");
        try {
            try {
                new JSONObject(message);
                // wrap json into code block
                message = "```" + message + "```";
            } catch (Exception ignore) {
            }
            telegramService.sendMessage(getUsers(workspaceBlock), escape(message), buttons);
        } catch (Exception ex) {
            workspaceBlock.logError("Error send telegram message " + ex);
        }
    }

    private void sendMessageCommand(WorkspaceBlock workspaceBlock) {
        Level level = workspaceBlock.getMenuValue(LEVEL, this.levelMenu);
        String message = workspaceBlock.getInputString(MESSAGE);
        try {
            try {
                new JSONObject(message);
                // wrap json into code block
                message = "```" + message + "```";
            } catch (Exception ignore) {
            }
            telegramService.sendMessage(getUsers(workspaceBlock), level.format(escape(message)));
        } catch (Exception ex) {
            workspaceBlock.logError("Error send telegram message " + ex);
        }
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

    private List<UserEntity> getUsers(WorkspaceBlock workspaceBlock) {
        String user = workspaceBlock.getMenuValue(USER, this.telegramUsersMenu);
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
        return users;
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

    @AllArgsConstructor
    private enum ByteType {
        Image(10 * 1024 * 1024),
        Video(50 * 1024 * 1024);
        private final int maxLength;
    }
}
