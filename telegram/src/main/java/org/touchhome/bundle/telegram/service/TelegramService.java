package org.touchhome.bundle.telegram.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.telegram.commands.*;
import org.touchhome.bundle.telegram.settings.TelegramBotNameSetting;
import org.touchhome.bundle.telegram.settings.TelegramBotTokenSetting;
import org.touchhome.bundle.telegram.settings.TelegramRestartBotButtonSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.touchhome.bundle.telegram.commands.TelegramBaseCommand.CHAT_ID;

@Log4j2
@Component
@RequiredArgsConstructor
public class TelegramService {

    private final TelegramBotsApi botsApi;
    private final DefaultBotOptions botOptions;
    private final Map<String, TelegramEventCommand> eventCommandMap = new HashMap<>();

    private final EntityContext entityContext;
    private BotSession botSession;
    private int replayId = 0;

    @Getter
    private TelegramBot telegramBot;

    public TelegramService(EntityContext entityContext) throws TelegramApiException {
        this.entityContext = entityContext;
        botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botOptions = new DefaultBotOptions();
    }

    public void postConstruct() {
        entityContext.setting().listenValue(TelegramRestartBotButtonSetting.class, "tm-fire-restart", this::restart);
        start();
    }

    public List<UserEntity> getUsers() {
        return entityContext.findAll(UserEntity.class).stream()
                .filter(u -> u.getUserType() == UserEntity.UserType.OTHER && u.getJsonData().has(CHAT_ID))
                .collect(Collectors.toList());
    }

    private void restart() {
        if (botSession != null && botSession.isRunning()) {
            botSession.stop();
        }
        this.start();
    }

    @SneakyThrows
    public void sendPhoto(List<UserEntity> users, InputFile inputFile, String caption) {
        for (UserEntity user : users) {
            SendPhoto sendPhoto = new SendPhoto(user.getJsonData().getString(CHAT_ID), inputFile);
            if (caption != null) {
                sendPhoto.setCaption(caption);
            }
            this.telegramBot.execute(sendPhoto);
        }
    }

    @SneakyThrows
    public void sendVideo(List<UserEntity> users, InputFile inputFile, String caption) {
        for (UserEntity user : users) {
            SendVideo sendVideo = new SendVideo(user.getJsonData().getString(CHAT_ID), inputFile);
            if (caption != null) {
                sendVideo.setCaption(caption);
            }
            this.telegramBot.execute(sendVideo);
        }
    }

    @SneakyThrows
    public void sendMessage(List<UserEntity> users, String message, String... buttons) {
        if (users != null && !users.isEmpty()) {
            if (message != null) {
                List<List<InlineKeyboardButton>> keyboard2D = new ArrayList<>();
                if (buttons.length > 0) {
                    String replyId = "rp_" + replayId++;
                    List<InlineKeyboardButton> keyboard = new ArrayList<>(buttons.length);
                    keyboard2D.add(keyboard);
                    for (String button : buttons) {
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(button);
                        inlineKeyboardButton.setCallbackData(replyId + " " + button);
                        keyboard.add(inlineKeyboardButton);
                    }
                }

                for (UserEntity user : users) {
                    SendMessage sendMessage = new SendMessage(user.getJsonData().getString(CHAT_ID), message);
                    if (!keyboard2D.isEmpty()) {
                        InlineKeyboardMarkup keyBoardMarkup = new InlineKeyboardMarkup(keyboard2D);
                        sendMessage.setReplyMarkup(keyBoardMarkup);
                    }
                    sendMessage.enableMarkdownV2(true);
                    Message execute = this.telegramBot.execute(sendMessage);

                    System.out.println(execute.getText());
                }
            }
        }
    }

    private void start() {
        try {
            if (isNotEmpty(entityContext.setting().getValue(TelegramBotNameSetting.class)) &&
                    isNotEmpty(entityContext.setting().getValue(TelegramBotTokenSetting.class))) {
                this.telegramBot = new TelegramBot(botOptions);
                this.botSession = botsApi.registerBot(this.telegramBot);
                log.info("Telegram bot started");
                entityContext.ui().sendInfoMessage("Telegram bot started");
            } else {
                log.warn("Telegram bot not started. Requires settings.");
                entityContext.ui().sendInfoMessage("Telegram bot started. Requires settings.");

            }
        } catch (Exception ex) {
            entityContext.ui().sendErrorMessage("Unable to start telegram bot: ", ex);
            log.error("Unable to start telegram bot", ex);
        }
    }

    public final class TelegramBot extends TelegramLongPollingCommandBot {

        TelegramBot(DefaultBotOptions botOptions) {
            super(botOptions);

            register(new TelegramStartCommand(this));
            register(new TelegramHelpCommand(this));
            register(new TelegramRegisterUserCommand(entityContext));
            register(new TelegramUnregisterUserCommand(entityContext));

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

        @Override
        public String getBotUsername() {
            return entityContext.setting().getValue(TelegramBotNameSetting.class);
        }

        @Override
        public String getBotToken() {
            return entityContext.setting().getValue(TelegramBotTokenSetting.class);
        }

        // handle message not started with '/'
        @Override
        public void processNonCommandUpdate(Update update) {
            log.info("Unable to process message {}", update.getMessage().getText());
        }

        public void registerEvent(String command, String description, String workspaceId, BroadcastLock lock) {
            TelegramEventCommand telegramEventCommand = eventCommandMap.computeIfAbsent(command, s -> {
                TelegramEventCommand eventCommand = new TelegramEventCommand(s, description, entityContext);
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
    }
}
