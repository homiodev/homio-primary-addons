package org.touchhome.bundle.telegram.workspace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.KeyValueEnum;
import org.touchhome.bundle.api.state.RawType;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.BlockType;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.touchhome.bundle.telegram.TelegramEntity;
import org.touchhome.bundle.telegram.TelegramEntryPoint;
import org.touchhome.bundle.telegram.service.TelegramAnswer;
import org.touchhome.bundle.telegram.service.TelegramService;
import org.touchhome.bundle.telegram.setting.TelegramSettingMaxQuestionMaxSeconds;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.touchhome.bundle.telegram.service.TelegramService.TELEGRAM_EVENT_PREFIX;

@Component
public class Scratch3TelegramBlocks extends Scratch3ExtensionBlocks {

    public static final String URL = "rest/telegram/";
    public static final String COMMAND = "COMMAND";
    public static final String DESCR = "DESCR";
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[\\{\\}\\.\\+\\-\\#\\(\\)]");
    private static final String USER = "USER";
    private static final String MESSAGE = "MESSAGE";
    private static final String LEVEL = "LEVEL";

    private final TelegramService telegramService;

    private final MenuBlock.StaticMenuBlock<Level> levelMenu;
    private final MenuBlock.StaticMenuBlock<ByteType> byteType;
    private final MenuBlock.ServerMenuBlock telegramEntityUsersMenu;
    private final MenuBlock.StaticMenuBlock<QuestionButtons> buttonsMenu;

    private final Scratch3Block sendMessageCommand;
    private final Scratch3Block getCommand;
    private final BroadcastLockManager broadcastLockManager;
    private final Scratch3Block sendQuestionSplitCommand;
    private final Scratch3Block sendImageMessageCommand;
    private final Scratch3Block sendQuestionNoSplitCommand;
    private final Scratch3Block sendQuestionSplitElseCommand;

    public Scratch3TelegramBlocks(TelegramService telegramService, EntityContext entityContext,
                                  BroadcastLockManager broadcastLockManager,
                                  TelegramEntryPoint telegramEntryPoint) {
        super("#73868c", entityContext, telegramEntryPoint);
        setParent("communication");
        this.telegramService = telegramService;
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.telegramEntityUsersMenu = MenuBlock.ofServer("telegramEntityUsersMenu", "rest/telegram/entityUser",
                "-", "-");
        this.levelMenu = MenuBlock.ofStatic("levelMenu", Level.class, Level.info);
        this.buttonsMenu = MenuBlock.ofStaticKV("buttonsMenu", QuestionButtons.class, QuestionButtons.YesNo);
        this.byteType = MenuBlock.ofStatic("type", ByteType.class, ByteType.Image);


        this.getCommand = Scratch3Block.ofHandler(10, "get_msg", BlockType.hat, "On command [COMMAND] | Desc: [DESCR]", this::whenGetMessage);
        this.getCommand.addArgument(COMMAND, "bulb4_on");
        this.getCommand.addArgument(DESCR, "Turn on bulb 4");

        this.sendMessageCommand = Scratch3Block.ofHandler(20, "send_msg", BlockType.command,
                "Send [MESSAGE] to [USER]. [LEVEL]", this::sendMessageCommand);
        this.sendMessageCommand.addArgument(MESSAGE, "msg");
        this.sendMessageCommand.addArgument(USER, this.telegramEntityUsersMenu);
        this.sendMessageCommand.addArgument(LEVEL, this.levelMenu);

        this.sendImageMessageCommand = Scratch3Block.ofHandler(30, "send_img", BlockType.command,
                "Send [TYPE] [MESSAGE], caption [CAPTION] to [USER]", this::sendIVAMessageCommand);
        this.sendImageMessageCommand.addArgument("TYPE", this.byteType);
        this.sendImageMessageCommand.addArgument(MESSAGE, "msg");
        this.sendImageMessageCommand.addArgument(USER, this.telegramEntityUsersMenu);
        this.sendImageMessageCommand.addArgument("CAPTION", "caption");

        this.sendQuestionNoSplitCommand = ask(Scratch3Block.ofHandler(40, "send_question", BlockType.command,
                "Ask&Wait [MESSAGE] to [USER] | [BUTTONS]", this::sendQuestionNoSplitCommand));

        this.sendQuestionSplitCommand = ask(Scratch3Block.ofConditional(41, "send_question_if",
                "Ask&Wait [MESSAGE] to [USER] | [BUTTONS]", this::sendQuestionSplitCommand));

        this.sendQuestionSplitElseCommand = ask(Scratch3Block.ofConditional(42, "send_question_if_else",
                "Ask&Wait [MESSAGE] to [USER] | [BUTTONS]", this::sendQuestionSplitElseCommand).addBranch("else"));
    }

    private Scratch3Block ask(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(MESSAGE, "msg");
        scratch3Block.addArgument(USER, this.telegramEntityUsersMenu);
        scratch3Block.addArgument("BUTTONS", this.buttonsMenu);
        return scratch3Block;
    }

    @AllArgsConstructor
    private enum QuestionButtons implements KeyValueEnum {
        YesNo("Yes/No"),
        OkCancel("Ok/Cancel"),
        ApproveDiscard("Approve/Discard");

        @Getter
        private final String value;
    }

    private void sendIVAMessageCommand(WorkspaceBlock workspaceBlock) {
        Pair<TelegramEntity, List<TelegramEntity.TelegramUser>> context = getEntityAndUsers(workspaceBlock);
        ByteType byteType = workspaceBlock.getMenuValue("TYPE", this.byteType);
        String caption = workspaceBlock.getInputString("CAPTION");
        RawType rawType = workspaceBlock.getInputRawType(MESSAGE, byteType.maxLength);
        InputFile inputFile = new InputFile(new ByteArrayInputStream(rawType.byteArrayValue()), rawType.getName());
        try {
            if (ByteType.Image.equals(byteType)) {
                telegramService.sendPhoto(context.getFirst(), context.getSecond(), inputFile, caption);
            } else {
                telegramService.sendVideo(context.getFirst(), context.getSecond(), inputFile, caption);
            }
        } catch (Exception ex) {
            workspaceBlock.logError("Error send telegram message " + ex);
        }
    }

    private void sendQuestionSplitCommand(WorkspaceBlock workspaceBlock) {
        sendQuestionCommand(workspaceBlock, workspaceBlock.getChildOrThrow(), null);
    }

    private void sendQuestionNoSplitCommand(WorkspaceBlock workspaceBlock) {
        sendQuestionCommand(workspaceBlock, null, null);
    }

    private void sendQuestionSplitElseCommand(WorkspaceBlock workspaceBlock) {
        sendQuestionCommand(workspaceBlock, workspaceBlock.getChildOrThrow(), workspaceBlock.getInputWorkspaceBlock("SUBSTACK2"));
    }

    private void sendQuestionCommand(WorkspaceBlock workspaceBlock, WorkspaceBlock nextBlock, WorkspaceBlock elseBlock) {
        String[] buttons = workspaceBlock.getMenuValue("BUTTONS", this.buttonsMenu).getValue().split("/");
        Message message = sendTelegramMessage(workspaceBlock, buttons, Function.identity());
        if (message != null) {
            BroadcastLock lock = this.broadcastLockManager.getOrCreateLock(workspaceBlock,
                    TELEGRAM_EVENT_PREFIX + message.getMessageId());
            workspaceBlock.waitForLock(lock, entityContext.setting().getValue(TelegramSettingMaxQuestionMaxSeconds.class),
                    TimeUnit.SECONDS, () -> {
                        if (((TelegramAnswer) lock.getValue()).getData().equals(buttons[0])) {
                            if (nextBlock != null) {
                                nextBlock.handle();
                            }
                        } else if (elseBlock != null) {
                            elseBlock.handle();
                        }
                    });
        }
    }

    private Message sendTelegramMessage(WorkspaceBlock workspaceBlock, String[] buttons, Function<String, String> messagePreUpdate) {
        Pair<TelegramEntity, List<TelegramEntity.TelegramUser>> context = getEntityAndUsers(workspaceBlock);
        String message = workspaceBlock.getInputString(MESSAGE);
        try {
            try {
                new JSONObject(message);
                // wrap json into code block
                message = "```" + message + "```";
            } catch (Exception ignore) {
            }
            return telegramService.sendMessage(context.getFirst(), context.getSecond(), messagePreUpdate.apply(escape(message)), buttons);
        } catch (Exception ex) {
            workspaceBlock.logError("Error send telegram message " + ex);
        }
        return null;
    }

    private void sendMessageCommand(WorkspaceBlock workspaceBlock) {
        Level level = workspaceBlock.getMenuValue(LEVEL, this.levelMenu);
        sendTelegramMessage(workspaceBlock, null, level::format);
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
        workspaceBlock.handleNext(next -> {
            String command = workspaceBlock.getInputString(COMMAND);
            String description = workspaceBlock.getInputString(DESCR);
            BroadcastLock lock = broadcastLockManager.getOrCreateLock(workspaceBlock);
            this.telegramService.registerEvent(command, description, workspaceBlock.getId(), lock);
            workspaceBlock.subscribeToLock(lock, next::handle);
        });
    }

    private Pair<TelegramEntity, List<TelegramEntity.TelegramUser>> getEntityAndUsers(WorkspaceBlock workspaceBlock) {
        String entityUser = workspaceBlock.getMenuValue(USER, this.telegramEntityUsersMenu);
        String[] entityAndUser = entityUser.split("/");
        TelegramEntity telegramEntity = entityContext.getEntity(entityAndUser[0]);
        if (telegramEntity == null) {
            workspaceBlock.logErrorAndThrow("Unable to find telegram: <{}>", entityAndUser[0]);
        }
        if (entityAndUser.length > 1) {
            return Pair.of(telegramEntity, Collections.singletonList(telegramEntity.getUser(Long.parseLong(entityAndUser[1]))));
        } else {
            List<TelegramEntity.TelegramUser> users = telegramEntity.getUsers();
            if (users.isEmpty()) {
                workspaceBlock.logWarn("Unable to find any registered users. Please open telegram bot and run command '/register'");
            }
            return Pair.of(telegramEntity, users);
        }
    }

    @RequiredArgsConstructor
    private enum Level {
        none(new byte[0]),
        info(new byte[]{-30, -124, -71, -17, -72, -113}),
        warn(new byte[]{-30, -102, -96, -17, -72, -113}),
        error(new byte[]{-16, -97, -122, -104});

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
