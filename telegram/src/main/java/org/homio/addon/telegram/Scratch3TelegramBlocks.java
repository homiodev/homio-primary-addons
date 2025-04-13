package org.homio.addon.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.telegram.service.TelegramAnswer;
import org.homio.addon.telegram.service.TelegramService;
import org.homio.api.Context;
import org.homio.api.model.OptionModel;
import org.homio.api.state.RawType;
import org.homio.api.state.State;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.selection.dynamic.DynamicOptionLoader;
import org.homio.api.ui.field.selection.dynamic.UIFieldDynamicSelection;
import org.homio.api.workspace.Lock;
import org.homio.api.workspace.WorkspaceBlock;
import org.homio.api.workspace.scratch.Scratch3Block;
import org.homio.api.workspace.scratch.Scratch3Block.ScratchSettingBaseEntity;
import org.homio.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.homio.addon.telegram.service.TelegramService.TELEGRAM_EVENT_PREFIX;

@Component
public class Scratch3TelegramBlocks extends Scratch3ExtensionBlocks {

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[{}.+\\-#()]");
    private static final String COMMAND = "COMMAND";
    private static final String MESSAGE = "MESSAGE";

    private final TelegramService telegramService;

    public Scratch3TelegramBlocks(TelegramService telegramService, Context context,
                                  TelegramEntrypoint telegramEntrypoint) {
        super("#73868c", context, telegramEntrypoint);
        setParent(ScratchParent.communication);
        this.telegramService = telegramService;

        blockHat(10, "get_msg", "On command [COMMAND] | [SETTING]", this::whenGetMessage, block -> {
            block.addArgument(COMMAND, "bulb_on");
            block.addSetting(OnGetMessageSetting.class);
        });

        blockCommand(20, "send_msg", "Send [MESSAGE] | [SETTING]", this::sendMessageCommand, block -> {
            block.addArgument(MESSAGE, "msg");
            block.addSetting(SendMessageSetting.class);
        });

        blockCommand(30, "send_img", "Send media [MESSAGE] | Caption: [SETTING]", this::sendIVAMessageCommand,
                block -> block.addSetting(SendImageSetting.class));

        ask(blockCommand(40, "send_question", "Ask&Wait [MESSAGE] | [SETTING]", this::sendQuestionNoSplitCommand));

        ask(blockCondition(50, "send_question_if", "Ask&Wait [MESSAGE] | [SETTING]", this::sendQuestionSplitCommand));

        ask(blockCondition(60, "send_question_if_else", "Ask&Wait [MESSAGE] | [SETTING]", this::sendQuestionSplitElseCommand).addBranch("else"));
    }

    private static String escape(String text) {
        Matcher matcher = ESCAPE_PATTERN.matcher(text);
        StringBuilder noteBuffer = new StringBuilder();
        while (matcher.find()) {
            String group = matcher.group();
            matcher.appendReplacement(noteBuffer, "\\\\" + group);
        }
        matcher.appendTail(noteBuffer);
        return noteBuffer.isEmpty() ? text : noteBuffer.toString();
    }

    private void ask(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(MESSAGE, "msg");
        scratch3Block.addSetting(QuestionSettings.class);
    }

    private void sendIVAMessageCommand(WorkspaceBlock workspaceBlock) {
        SendImageSetting setting = workspaceBlock.getSetting(SendImageSetting.class);
        TelegramUser context = getEntityAndUsers(workspaceBlock, setting.telegramEntity);
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
        TelegramUser context = getEntityAndUsers(workspaceBlock, setting.telegramEntity);
        String[] buttons = new String[]{setting.okButton, setting.noButton};
        Message message = sendTelegramMessage(workspaceBlock, buttons, Function.identity(), context);
        if (message != null) {
            Lock lock = workspaceBlock.getLockManager().getLock(workspaceBlock,
                    TELEGRAM_EVENT_PREFIX + message.getMessageId());
            workspaceBlock.waitForLock(lock, context.telegramEntity.getWaitQuestionMaxSeconds(),
                    TimeUnit.SECONDS, () -> {
                        State value = (State) lock.getValue();
                        if (((TelegramAnswer) value.rawValue()).getData().equals(buttons[0])) {
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
        SendMessageSetting setting = workspaceBlock.getSetting(SendMessageSetting.class);
        TelegramUser context = getEntityAndUsers(workspaceBlock, setting.telegramEntity);
        sendTelegramMessage(workspaceBlock, null, setting.level::format, context);
    }

    private void whenGetMessage(WorkspaceBlock workspaceBlock) {
        workspaceBlock.handleNext(next -> {
            OnGetMessageSetting setting = workspaceBlock.getSetting(OnGetMessageSetting.class);
            String command = workspaceBlock.getInputString(COMMAND);
            Lock lock = workspaceBlock.getLockManager().getLock(workspaceBlock);
            telegramService.registerEvent(command, setting.description, workspaceBlock.getId(), lock);
            lock.addReleaseListener(workspaceBlock.getBlockId() + "pending",
                    () -> telegramService.unregisterEvent(command, workspaceBlock.getId()));
            workspaceBlock.subscribeToLock(lock, next::handle);
        });
    }

    private TelegramUser getEntityAndUsers(WorkspaceBlock workspaceBlock, String telegramEntityID) {
        String[] entityAndUser = telegramEntityID.split("/");
        TelegramEntity telegramEntity = context.db().getRequire(entityAndUser[0]);
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
    public enum Level {
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

    private record TelegramUser(TelegramEntity telegramEntity, List<TelegramEntity.TelegramUser> users) {

    }

    @Getter
    @Setter
    public static class OnGetMessageSetting implements ScratchSettingBaseEntity {

        @UIField(order = 1)
        private String description = "Description example";

        @UIField(order = 2, required = true)
        @UIFieldDynamicSelection(SelectTelegramUsersOptionLoader.class)
        private String telegramEntity = TelegramEntity.PRIMARY_ENTITY_ID;
    }

    @Getter
    @Setter
    public static class SendImageSetting implements ScratchSettingBaseEntity {

        @UIField(order = 1)
        private String caption;

        @UIField(order = 2, required = true)
        @UIFieldDynamicSelection(SelectTelegramUsersOptionLoader.class)
        private String telegramEntity = TelegramEntity.PRIMARY_ENTITY_ID;
    }

    @Getter
    @Setter
    public static class SendMessageSetting implements ScratchSettingBaseEntity {

        @UIField(order = 1)
        private Level level = Level.info;

        @UIField(order = 2, required = true)
        @UIFieldDynamicSelection(SelectTelegramUsersOptionLoader.class)
        private String telegramEntity = TelegramEntity.PRIMARY_ENTITY_ID;
    }

    @Getter
    @Setter
    public static class QuestionSettings implements ScratchSettingBaseEntity {

        @UIField(order = 1, icon = "fa fa-check", color = Color.GREEN)
        private String okButton = "Yes";

        @UIField(order = 2, icon = "fa fa-xmark", color = Color.RED)
        private String noButton = "No";

        @UIField(order = 3, required = true)
        @UIFieldDynamicSelection(SelectTelegramUsersOptionLoader.class)
        private String telegramEntity = TelegramEntity.PRIMARY_ENTITY_ID;
    }

    public static class SelectTelegramUsersOptionLoader implements DynamicOptionLoader {

        @Override
        public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
            List<OptionModel> models = new ArrayList<>();
            for (TelegramEntity telegramEntity : parameters.context().db().findAll(TelegramEntity.class)) {
                models.add(OptionModel.of(telegramEntity.getEntityID(), telegramEntity.getTitle() + "/All"));
                telegramEntity.getUsers().forEach(user ->
                        models.add(OptionModel.of(telegramEntity.getEntityID() + "/" + user.getId(),
                                telegramEntity.getTitle() + "/" + user.getName())));
            }
            return models;
        }
    }
}
