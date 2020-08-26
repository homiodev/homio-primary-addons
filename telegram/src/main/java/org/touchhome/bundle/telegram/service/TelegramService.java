package org.touchhome.bundle.telegram.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.UserEntity;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.telegram.commands.TelegramEventCommand;
import org.touchhome.bundle.telegram.commands.TelegramHelpCommand;
import org.touchhome.bundle.telegram.commands.TelegramRegisterUserCommand;
import org.touchhome.bundle.telegram.commands.TelegramUnregisterUserCommand;
import org.touchhome.bundle.telegram.settings.TelegramBotNameSetting;
import org.touchhome.bundle.telegram.settings.TelegramBotTokenSetting;
import org.touchhome.bundle.telegram.settings.TelegramRestartBotButtonSetting;

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

    static {
        ApiContextInitializer.init();
    }

    private final TelegramBotsApi botsApi = new TelegramBotsApi();
    private final DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
    private final Map<String, TelegramEventCommand> eventCommandMap = new HashMap<>();

    private final EntityContext entityContext;
    private BotSession botSession;
    private RestTemplate restTemplate = new RestTemplate();

    @Getter
    private TelegramBot telegramBot;

    public void postConstruct() {
        ApiContextInitializer.init();
        entityContext.listenSettingValue(TelegramRestartBotButtonSetting.class, this::restart);
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

    public void sendMessage(List<UserEntity> users, String message) {
        if (users != null && !users.isEmpty()) {
            String token = entityContext.getSettingValue(TelegramBotTokenSetting.class);
            for (UserEntity user : users) {
                restTemplate.postForObject("https://api.telegram.org/bot" + token + "/sendMessage", new SendMessageData(user.getUserId(), message), Object.class);
            }
        }
    }

    private void start() {
        try {
            if (isNotEmpty(entityContext.getSettingValue(TelegramBotNameSetting.class)) &&
                    isNotEmpty(entityContext.getSettingValue(TelegramBotTokenSetting.class))) {
                this.telegramBot = new TelegramBot(botOptions);
                this.botSession = botsApi.registerBot(this.telegramBot);
                log.info("Telegram bot started");
                entityContext.sendInfoMessage("Telegram bot started");
            } else {
                log.warn("Telegram bot not started. Requires settings.");
                entityContext.sendInfoMessage("Telegram bot started. Requires settings.");

            }
        } catch (Exception ex) {
            entityContext.sendErrorMessage("Unable to start telegram bot: ", ex);
            log.error("Unable to start telegram bot", ex);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class SendMessageData {
        private String chat_id;
        private String text;
    }

    public final class TelegramBot extends TelegramLongPollingCommandBot {

        TelegramBot(DefaultBotOptions botOptions) {
            super(botOptions);

            register(new TelegramHelpCommand(this));
            register(new TelegramRegisterUserCommand(entityContext));
            register(new TelegramUnregisterUserCommand(entityContext));

            log.info("Registering default action'...");
            registerDefaultAction(((absSender, message) -> {

                log.warn("Telegram User {} is trying to execute unknown command '{}'.", message.getFrom().getId(), message.getText());

                SendMessage text = new SendMessage();
                text.setChatId(message.getChatId());
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
            return entityContext.getSettingValue(TelegramBotNameSetting.class);
        }

        @Override
        public String getBotToken() {
            return entityContext.getSettingValue(TelegramBotTokenSetting.class);
        }

        // handle message not started with '/'
        @Override
        public void processNonCommandUpdate(Update update) {
            log.info("Processing non-command update...");
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
