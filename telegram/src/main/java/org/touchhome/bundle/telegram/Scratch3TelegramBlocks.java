package org.touchhome.bundle.telegram;

import static org.touchhome.bundle.telegram.service.TelegramService.TELEGRAM_EVENT_PREFIX;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.KeyValueEnum;
import org.touchhome.bundle.api.state.RawType;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.touchhome.bundle.telegram.service.TelegramAnswer;
import org.touchhome.bundle.telegram.service.TelegramService;
import org.touchhome.bundle.telegram.setting.TelegramSettingMaxQuestionMaxSeconds;

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
  private final MenuBlock.ServerMenuBlock telegramEntityUsersMenu;
  private final MenuBlock.StaticMenuBlock<QuestionButtons> buttonsMenu;

  public Scratch3TelegramBlocks(TelegramService telegramService, EntityContext entityContext,
      TelegramEntrypoint telegramEntrypoint) {
    super("#73868c", entityContext, telegramEntrypoint);
    setParent("communication");
    this.telegramService = telegramService;

    // Menu
    this.telegramEntityUsersMenu = menuServer("telegramEntityUsersMenu", "rest/telegram/entityUser", "Telegram");
    this.levelMenu = menuStatic("levelMenu", Level.class, Level.info);
    this.buttonsMenu = menuStaticKV("buttonsMenu", QuestionButtons.class, QuestionButtons.YesNo);

    blockHat(10, "get_msg", "On command [COMMAND] | Desc: [DESCR]",
        this::whenGetMessage, block -> {
          block.addArgument(COMMAND, "bulb4_on");
          block.addArgument(DESCR, "Turn on bulb 4");
        });

    blockCommand(20, "send_msg", "Send [MESSAGE] to [USER]. [LEVEL]", this::sendMessageCommand, block -> {
      block.addArgument(MESSAGE, "msg");
      block.addArgument(USER, this.telegramEntityUsersMenu);
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

  private Scratch3Block ask(Scratch3Block scratch3Block) {
    scratch3Block.addArgument(MESSAGE, "msg");
    scratch3Block.addArgument(USER, this.telegramEntityUsersMenu);
    scratch3Block.addArgument("BUTTONS", this.buttonsMenu);
    return scratch3Block;
  }

  private void sendIVAMessageCommand(WorkspaceBlock workspaceBlock) {
    Pair<TelegramEntity, List<TelegramEntity.TelegramUser>> context = getEntityAndUsers(workspaceBlock);
    String caption = workspaceBlock.getInputString("CAPTION");
    RawType rawType = workspaceBlock.getInputRawType(MESSAGE, 50 * 1024 * 1024);
    InputFile inputFile = new InputFile(new ByteArrayInputStream(rawType.byteArrayValue()),
        StringUtils.defaultString(rawType.getName(), "Undefined name"));
    try {
      if (rawType.isImage()) {
        telegramService.sendPhoto(context.getFirst(), context.getSecond(), inputFile, caption);
      } else if (rawType.isVideo()) {
        telegramService.sendVideo(context.getFirst(), context.getSecond(), inputFile, caption);
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
    String[] buttons = workspaceBlock.getMenuValue("BUTTONS", this.buttonsMenu).getValue().split("/");
    Message message = sendTelegramMessage(workspaceBlock, buttons, Function.identity());
    if (message != null) {
      BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock,
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

  private Message sendTelegramMessage(WorkspaceBlock workspaceBlock, String[] buttons,
      Function<String, String> messagePreUpdate) {
    Pair<TelegramEntity, List<TelegramEntity.TelegramUser>> context = getEntityAndUsers(workspaceBlock);
    String message = workspaceBlock.getInputString(MESSAGE);
    try {
      try {
        new JSONObject(message);
        // wrap json into code block
        message = "```" + message + "```";
      } catch (Exception ignore) {
      }
      return telegramService.sendMessage(context.getFirst(), context.getSecond(), messagePreUpdate.apply(escape(message)),
          buttons);
    } catch (Exception ex) {
      workspaceBlock.logError("Error send telegram message " + ex);
    }
    return null;
  }

  private void sendMessageCommand(WorkspaceBlock workspaceBlock) {
    Level level = workspaceBlock.getMenuValue(LEVEL, this.levelMenu);
    sendTelegramMessage(workspaceBlock, null, level::format);
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
        workspaceBlock.logWarn(
            "Unable to find any registered users. Please open telegram bot and run command '/register'");
      }
      return Pair.of(telegramEntity, users);
    }
  }

  @AllArgsConstructor
  private enum QuestionButtons implements KeyValueEnum {
    YesNo("Yes/No"),
    OkCancel("Ok/Cancel"),
    ApproveDiscard("Approve/Discard");

    @Getter
    private final String value;
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

    /*@AllArgsConstructor
    private enum ByteType {
        Image(10 * 1024 * 1024),
        Video(50 * 1024 * 1024);
        private final int maxLength;
    }*/
}
