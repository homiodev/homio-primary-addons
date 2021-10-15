package org.touchhome.bundle.telegram.service;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.telegram.TelegramEntity;
import org.touchhome.bundle.telegram.commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Log4j2
@Component
public class TelegramService {

    public static final String TELEGRAM_EVENT_PREFIX = "telegram_";
    private final TelegramBotsApi botsApi;
    private final DefaultBotOptions botOptions;
    private final BroadcastLockManager broadcastLockManager;
    private final Map<String, TelegramEventCommand> eventCommandMap = new HashMap<>();

    private final EntityContext entityContext;
    private Map<String, TelegramBot> telegramBots = new HashMap<>();

    @SneakyThrows
    public TelegramService(EntityContext entityContext, BroadcastLockManager broadcastLockManager) {
        this.entityContext = entityContext;
        this.broadcastLockManager = broadcastLockManager;
        this.botsApi = new TelegramBotsApi(DefaultBotSession.class);
        this.botOptions = new DefaultBotOptions();
    }

    public void restart(TelegramEntity telegramEntity) {
        TelegramBot telegramBot = telegramBots.get(telegramEntity.getEntityID());
        if (telegramBot != null) {
            telegramBot.dispose();
        }
        start(telegramEntity);
    }

    private void start(TelegramEntity telegramEntity) {
        try {
            if (isNotEmpty(telegramEntity.getBotName()) && isNotEmpty(telegramEntity.getBotToken())) {
                TelegramBot telegramBot = new TelegramBot(botOptions, telegramEntity);
                this.telegramBots.put(telegramEntity.getEntityID(), telegramBot);
                log.info("Telegram bot running");
                entityContext.ui().sendInfoMessage("Telegram bot running");
                telegramEntity.setStatusOnline();
            } else {
                log.warn("Telegram bot not running. Requires settings.");
                entityContext.ui().sendWarningMessage("Telegram bot not running. Requires settings.");
                telegramEntity.setStatus(Status.ERROR, isEmpty(telegramEntity.getBotName()) ?
                        "Require bot name field" : "Require bot token field");

            }
        } catch (Exception ex) {
            entityContext.ui().sendErrorMessage("Unable to start telegram bot: ", ex);
            log.error("Unable to start telegram bot", ex);
            telegramEntity.setStatusError(ex);
        }
    }

    public void dispose(TelegramEntity telegramEntity) {
        TelegramBot telegramBot = telegramBots.remove(telegramEntity.getEntityID());
        if (telegramBot != null) {
            telegramBot.dispose();
        }
    }

    public void setTelegramEntity(TelegramEntity telegramEntity) {
        if (telegramBots.containsKey(telegramEntity.getEntityID())) {
            getTelegramBot(telegramEntity).telegramEntity = telegramEntity;
        }
    }

    public void sendPhoto(TelegramEntity telegramEntity, List<TelegramEntity.TelegramUser> users, InputFile inputFile, String caption) {
        getTelegramBot(telegramEntity).sendPhoto(checkUsers(users), inputFile, caption);
    }

    public void sendVideo(TelegramEntity telegramEntity, List<TelegramEntity.TelegramUser> users, InputFile inputFile, String caption) {
        getTelegramBot(telegramEntity).sendVideo(checkUsers(users), inputFile, caption);
    }

    public Message sendMessage(TelegramEntity telegramEntity, List<TelegramEntity.TelegramUser> users, String message, String[] buttons) {
        return getTelegramBot(telegramEntity).sendMessage(checkUsers(users), message, buttons);
    }

    private List<TelegramEntity.TelegramUser> checkUsers(List<TelegramEntity.TelegramUser> users) {
        if(users.isEmpty()) {
            throw new IllegalStateException("Telegram bot has no registered users");
        }
        return users;
    }

    private TelegramBot getTelegramBot(TelegramEntity telegramEntity) {
        TelegramBot telegramBot = telegramBots.get(telegramEntity.getEntityID());
        if (telegramBot == null) {
            throw new IllegalStateException("TelegramBot <" + telegramEntity.getTitle() + " not started");
        }
        return telegramBot;
    }

    public void registerEvent(String command, String description, String id, BroadcastLock lock) {
        for (TelegramBot telegramBot : telegramBots.values()) {
            telegramBot.registerEvent(command, description, id, lock);
        }
    }

    public final class TelegramBot extends TelegramLongPollingCommandBot {

        private final BotSession botSession;
        @Getter
        private TelegramEntity telegramEntity;

        @SneakyThrows
        TelegramBot(DefaultBotOptions botOptions, TelegramEntity telegramEntity) {
            super(botOptions);
            this.telegramEntity = telegramEntity;
            this.botSession = botsApi.registerBot(this);

            register(new TelegramStartCommand(this));
            register(new TelegramHelpCommand(this));
            register(new TelegramRegisterUserCommand(entityContext, this));
            register(new TelegramUnregisterUserCommand(entityContext, this));

            log.info("Registering default action'...");
            registerDefaultAction(((absSender, message) -> {

                log.warn("Telegram User {} is trying to execute unknown command '{}'.", message.getFrom().getId(), message.getText());

                SendMessage text = new SendMessage();
                text.setChatId(Long.toString(message.getChatId()));
                text.setText(message.getText() + " command not found!");

                try {
                    absSender.execute(text);
                } catch (TelegramApiException e) {
                    log.error("Error while replying unknown command to user {}.", message.getFrom(), e);
                }
            }));
        }

        @SneakyThrows
        public void sendPhoto(List<TelegramEntity.TelegramUser> users, InputFile inputFile, String caption) {
            for (TelegramEntity.TelegramUser user : users) {
                SendPhoto sendPhoto = new SendPhoto(user.getChatId(), inputFile);
                if (caption != null) {
                    sendPhoto.setCaption(caption);
                }
                this.execute(sendPhoto);
            }
        }

        @SneakyThrows
        public void sendVideo(List<TelegramEntity.TelegramUser> users, InputFile inputFile, String caption) {
            for (TelegramEntity.TelegramUser user : users) {
                SendVideo sendVideo = new SendVideo(user.getChatId(), inputFile);
                if (caption != null) {
                    sendVideo.setCaption(caption);
                }
                this.execute(sendVideo);
            }
        }

        @SneakyThrows
        public Message sendMessage(List<TelegramEntity.TelegramUser> users, String message, String[] buttons) {
            if (users != null && !users.isEmpty()) {
                if (message != null) {
                    List<List<InlineKeyboardButton>> keyboard2D = new ArrayList<>();
                    if (buttons != null && buttons.length > 0) {
                        List<InlineKeyboardButton> keyboard = new ArrayList<>(buttons.length);
                        keyboard2D.add(keyboard);
                        for (String button : buttons) {
                            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(button);
                            inlineKeyboardButton.setCallbackData(button);
                            keyboard.add(inlineKeyboardButton);
                        }
                    }

                    for (TelegramEntity.TelegramUser user : users) {
                        SendMessage sendMessage = new SendMessage(user.getChatId(), message);
                        if (!keyboard2D.isEmpty()) {
                            sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard2D));
                        }
                        sendMessage.enableMarkdownV2(true);
                        return this.execute(sendMessage);
                    }
                }
            }
            return null;
        }

        @Override
        public String getBotUsername() {
            return telegramEntity.getBotName();
        }

        @Override
        public String getBotToken() {
            return telegramEntity.getBotToken().asString();
        }

        // handle message not started with '/'
        @Override
        @SneakyThrows
        public void processNonCommandUpdate(Update update) {
            if (update.getCallbackQuery() != null) {
                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                TelegramAnswer telegramAnswer = new TelegramAnswer(messageId,
                        update.getCallbackQuery().getData(),
                        update.getCallbackQuery().getFrom().getId());
                TelegramService.this.broadcastLockManager.signalAll(TELEGRAM_EVENT_PREFIX + messageId, telegramAnswer);
                TelegramService.this.broadcastLockManager.signalAll(TELEGRAM_EVENT_PREFIX + messageId + "_" + telegramAnswer.getData(), telegramAnswer);
                AnswerCallbackQuery query = new AnswerCallbackQuery(update.getCallbackQuery().getId());
                query.setText("Done");
                this.execute(query);

                // remove buttons
                EditMessageReplyMarkup editReplyMarkup = new EditMessageReplyMarkup(Long.toString(update.getCallbackQuery().getFrom().getId()), update.getCallbackQuery().getMessage().getMessageId(), null,
                        new InlineKeyboardMarkup(new ArrayList<>()));
                this.execute(editReplyMarkup);
            }
            log.debug("Income message {}", update.getMessage() != null ?
                    update.getMessage().getText() : update.getCallbackQuery().getData());
        }

        public void registerEvent(String command, String description, String workspaceId, BroadcastLock lock) {
            TelegramEventCommand telegramEventCommand = eventCommandMap.computeIfAbsent(command, commandIdentifier -> {
                TelegramEventCommand eventCommand = new TelegramEventCommand(commandIdentifier, description,
                        this);
                this.register(eventCommand);
                return eventCommand;
            });
            telegramEventCommand.addHandler(workspaceId, lock);
            lock.addReleaseListener(workspaceId, () -> {
                telegramEventCommand.removeHandler(workspaceId);
                if (!telegramEventCommand.hasHandlers()) {
                    TelegramEventCommand eventCommand = eventCommandMap.remove(command);
                    if (eventCommand != null) {
                        this.deregister(eventCommand);
                    }
                }
            });
        }

        public void dispose() {
            if (botSession != null && botSession.isRunning()) {
                botSession.stop();
            }
        }
    }
}
