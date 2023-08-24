package org.homio.addon.telegram;

import static org.homio.addon.telegram.service.TelegramService.TELEGRAM_EVENT_PREFIX;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.telegram.service.TelegramAnswer;
import org.homio.addon.telegram.service.TelegramService;
import org.homio.api.EntityContext;
import org.homio.api.entity.EntityFieldMetadata;
import org.homio.api.state.RawType;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.field.UIField;
import org.homio.api.workspace.BroadcastLock;
import org.homio.api.workspace.WorkspaceBlock;
import org.homio.api.workspace.scratch.MenuBlock;
import org.homio.api.workspace.scratch.Scratch3Block;
import org.homio.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class Scratch3TelegramBlocks extends Scratch3ExtensionBlocks {

    public static final String COMMAND = "COMMAND";
    public static final String DESCR = "DESCR";
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[{}.+\\-#()]");
    private static final String USER = "USER";
    private static final String MESSAGE = "MESSAGE";
    private static final String LEVEL = "LEVEL";

    private final TelegramService telegramService;

    private final MenuBlock.StaticMenuBlock<Level> levelMenu;
    private final MenuBlock.ServerMenuBlock telegramEntityUsersMenu;

    public Scratch3TelegramBlocks(TelegramService telegramService, EntityContext entityContext,
                                  TelegramEntrypoint telegramEntrypoint) {
        super("#73868c", entityContext, telegramEntrypoint);
        setParent("communication");
        this.telegramService = telegramService;

        // Menu
        this.telegramEntityUsersMenu = menuServer("telegramEntityUsersMenu", "rest/telegram/entityUser", "Telegram");
        this.levelMenu = menuStatic("levelMenu", Level.class, Level.info);

        blockHat(10, "get_msg", "On command [COMMAND] of [USER] | Desc: [DESCR]",
                this::whenGetMessage, block -> {
                    block.addArgument(USER, this.telegramEntityUsersMenu);
                    block.addArgument(COMMAND, "bulb_on");
                    block.addArgument(DESCR, "Turn on bulb");
                });

        blockCommand(20, "send_msg", "Send [MESSAGE] to [USER]. [LEVEL]", this::sendMessageCommand, block -> {
            block.addArgument(USER, this.telegramEntityUsersMenu);
            block.addArgument(MESSAGE, "msg");
            block.addArgument(LEVEL, this.levelMenu);
        });

        blockCommand(30, "send_img", "Send media [MESSAGE] to [USER] | Caption: [CAPTION]", this::sendIVAMessageCommand, block -> {
            block.addArgument(USER, this.telegramEntityUsersMenu);
            block.addArgument("CAPTION", "caption");
        });

        ask(blockCommand(40, "send_question",
                "Ask&Wait [MESSAGE] to [USER] | [BUTTONS]", this::sendQuestionNoSplitCommand));

        ask(blockCondition(41, "send_question_if",
                "Ask&Wait [MESSAGE] to [USER] | [BUTTONS]", this::sendQuestionSplitCommand));

        ask(blockCondition(42, "send_question_if_else",
                "Ask&Wait [MESSAGE] to [USER] | [BUTTONS]", this::sendQuestionSplitElseCommand).addBranch("else"));
    }

    private static String escape(String text) {
        Matcher matcher = ESCAPE_PATTERN.matcher(text);
        StringBuilder noteBuffer = new StringBuilder();
        while (matcher.find()) {
            String group = matcher.group();
            matcher.appendReplacement(noteBuffer, "\\\\" + group);
        }
        matcher.appendTail(noteBuffer);
        return noteBuffer.length() == 0 ? text : noteBuffer.toString();
    }

    private void ask(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(MESSAGE, "msg");
        scratch3Block.addArgument(USER, this.telegramEntityUsersMenu);
        scratch3Block.addSetting(QuestionSettings.class);
    }

    private void sendIVAMessageCommand(WorkspaceBlock workspaceBlock) {
        TelegramUser context = getEntityAndUsers(workspaceBlock);
        String caption = workspaceBlock.getInputString("CAPTION");
        RawType rawType = workspaceBlock.getInputRawType(MESSAGE, 50 * 1024 * 1024);
        InputFile inputFile = new InputFile(new ByteArrayInputStream(rawType.byteArrayValue()),
                StringUtils.defaultString(rawType.getName(), "Undefined name"));
        try {
            if (rawType.isImage()) {
                telegramService.sendPhoto(context.telegramEntity, context.users, inputFile, caption);
            } else if (rawType.isVideo()) {
                telegramService.sendVideo(context.telegramEntity, context.users, inputFile, caption);
            } else {
                workspaceBlock.logErrorAndThrow("Unable to recognize media MimeType: <{}>", rawType.getMimeType());
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
        QuestionSettings setting = workspaceBlock.getSetting(QuestionSettings.class);
        TelegramUser context = getEntityAndUsers(workspaceBlock);
        String[] buttons = new String[]{setting.okButton, setting.noButton};
        Message message = sendTelegramMessage(workspaceBlock, buttons, Function.identity(), context);
        if (message != null) {
            BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock,
                    TELEGRAM_EVENT_PREFIX + message.getMessageId());
            workspaceBlock.waitForLock(lock, context.telegramEntity.getWaitQuestionMaxSeconds(),
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

    private Message sendTelegramMessage(WorkspaceBlock workspaceBlock, String[] buttons,
                                        Function<String, String> messagePreUpdate, TelegramUser context) {
        String message = workspaceBlock.getInputString(MESSAGE);
        try {
            try {
                new JSONObject(message);
                // wrap json into code block
                message = "```" + message + "```";
            } catch (Exception ignore) {
            }
            return telegramService.sendMessage(context.telegramEntity, context.users, messagePreUpdate.apply(escape(message)),
                    buttons);
        } catch (Exception ex) {
            workspaceBlock.logError("Error send telegram message " + ex);
        }
        return null;
    }

    private void sendMessageCommand(WorkspaceBlock workspaceBlock) {
        Level level = workspaceBlock.getMenuValue(LEVEL, this.levelMenu);
        TelegramUser context = getEntityAndUsers(workspaceBlock);
        sendTelegramMessage(workspaceBlock, null, level::format, context);
    }

    private void whenGetMessage(WorkspaceBlock workspaceBlock) {
        workspaceBlock.handleNext(next -> {
            String command = workspaceBlock.getInputString(COMMAND);
            String description = workspaceBlock.getInputString(DESCR);
            BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock);
            this.telegramService.registerEvent(command, description, workspaceBlock.getId(), lock);
            workspaceBlock.subscribeToLock(lock, next::handle);
        });
    }

    private @NotNull TelegramUser getEntityAndUsers(WorkspaceBlock workspaceBlock) {
        String entityUser = workspaceBlock.getMenuValue(USER, this.telegramEntityUsersMenu);
        String[] entityAndUser = entityUser.split("/");
        TelegramEntity telegramEntity = entityContext.getEntity(entityAndUser[0]);
        if (telegramEntity == null) {
            workspaceBlock.logErrorAndThrow("Unable to find telegram: <{}>", entityAndUser[0]);
        }
        if (entityAndUser.length > 1) {
            return new TelegramUser(telegramEntity, Collections.singletonList(telegramEntity.getUser(Long.parseLong(entityAndUser[1]))));
        } else {
            List<TelegramEntity.TelegramUser> users = telegramEntity.getUsers();
            if (users.isEmpty()) {
                workspaceBlock.logErrorAndThrow(
                        "Unable to find any registered users. Please open telegram bot and run command '/register'");
            }
            return new TelegramUser(telegramEntity, users);
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

    @RequiredArgsConstructor
    private static class TelegramUser {

        private final TelegramEntity telegramEntity;
        private final List<TelegramEntity.TelegramUser> users;
    }

    @Getter
    @Setter
    public static class QuestionSettings implements EntityFieldMetadata {

        @UIField(order = 1, icon = "fa fa-check", color = Color.GREEN)
        private String okButton = "Yes";

        @UIField(order = 2, icon = "fa fa-xmark", color = Color.RED)
        private String noButton = "No";

        @Override
        public @NotNull String getEntityID() {
            return "yes_no";
        }
    }
}
