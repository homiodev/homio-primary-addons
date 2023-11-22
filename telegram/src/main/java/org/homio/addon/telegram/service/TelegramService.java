package org.homio.addon.telegram.service;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.homio.addon.telegram.TelegramEntity;
import org.homio.addon.telegram.commands.TelegramEventCommand;
import org.homio.addon.telegram.commands.TelegramHelpCommand;
import org.homio.addon.telegram.commands.TelegramRegisterUserCommand;
import org.homio.addon.telegram.commands.TelegramStartCommand;
import org.homio.addon.telegram.commands.TelegramUnregisterUserCommand;
import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.state.ObjectType;
import org.homio.api.state.State;
import org.homio.api.util.CommonUtils;
import org.homio.api.workspace.Lock;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Log4j2
@Component
public class TelegramService {

    public static final String TELEGRAM_EVENT_PREFIX = "telegram_";
    private final TelegramBotsApi botsApi;
    private final Map<String, TelegramEventCommand> eventCommandMap = new HashMap<>();

    private final Context context;
    private final Map<String, TelegramBot> telegramBots = new HashMap<>();

    @SneakyThrows
    public TelegramService(Context context) {
        this.context = context;
        this.botsApi = new TelegramBotsApi(DefaultBotSession.class);
    }

    public void restart(TelegramEntity telegramEntity) {
        TelegramBot telegramBot = telegramBots.get(telegramEntity.getEntityID());
        if (telegramBot != null) {
            telegramBot.dispose();
        }
        start(telegramEntity);
    }

    public void dispose(TelegramEntity telegramEntity) {
        TelegramBot telegramBot = telegramBots.remove(telegramEntity.getEntityID());
        if (telegramBot != null) {
            telegramBot.dispose();
        }
    }

    public void entityUpdated(TelegramEntity telegramEntity) {
        if (telegramBots.containsKey(telegramEntity.getEntityID())) {
            getTelegramBot(telegramEntity).telegramEntity = telegramEntity;
        }
    }

    public void sendPhoto(TelegramEntity telegramEntity, List<TelegramEntity.TelegramUser> users, InputFile inputFile,
                          String caption) {
        getTelegramBot(telegramEntity).sendPhoto(checkUsers(users), inputFile, caption);
    }

    public void sendVideo(TelegramEntity telegramEntity, List<TelegramEntity.TelegramUser> users, InputFile inputFile,
                          String caption) {
        getTelegramBot(telegramEntity).sendVideo(checkUsers(users), inputFile, caption);
    }

    public Message sendMessage(TelegramEntity telegramEntity, List<TelegramEntity.TelegramUser> users, String message,
                               String[] buttons) {
        return getTelegramBot(telegramEntity).sendMessage(checkUsers(users), message, buttons);
    }

    public TelegramBot getTelegramBot(TelegramEntity telegramEntity) {
        TelegramBot telegramBot = telegramBots.get(telegramEntity.getEntityID());
        if (telegramBot == null) {
            throw new IllegalStateException("TelegramBot <" + telegramEntity.getTitle() + " not started");
        }
        return telegramBot;
    }

    public void registerEvent(String command, String description, String id, Lock lock) {
        for (TelegramBot telegramBot : telegramBots.values()) {
            telegramBot.registerEvent(command, description, id, lock);
        }
    }

    public void updateNotificationBlock(TelegramEntity entity) {
        context.ui().notification().addBlockOptional("telegram", "Telegram", new Icon("fab fa-telegram", "#0088CC"));
        context.ui().notification().updateBlock("telegram", entity);
    }

    private void start(TelegramEntity telegramEntity) {
        try {
            if (isNotEmpty(telegramEntity.getBotName()) && isNotEmpty(telegramEntity.getBotToken())) {
                DefaultBotOptions botOptions = new DefaultBotOptions();
                botOptions.setProxyType(telegramEntity.getProxyType());
                botOptions.setProxyHost(telegramEntity.getProxyHost());
                botOptions.setProxyPort(telegramEntity.getProxyPort());
                botOptions.setGetUpdatesTimeout(telegramEntity.getUpdateTimeout());

                TelegramBot telegramBot = new TelegramBot(botOptions, telegramEntity);
                this.telegramBots.put(telegramEntity.getEntityID(), telegramBot);
                log.info("Telegram bot running");
                context.ui().toastr().info("Telegram bot running");
                telegramEntity.setStatusOnline();
            } else {
                log.warn("Telegram bot not running. Requires settings.");
                context.ui().toastr().warn("Telegram bot not running. Requires settings.");
                telegramEntity.setStatus(Status.ERROR, isEmpty(telegramEntity.getBotName()) ?
                        "Require bot name field" : "Require bot token field");
            }
        } catch (Exception ex) {
            String msg = "Unable to start telegram bot: " + CommonUtils.getErrorMessage(ex);
            if (ex.getCause() instanceof TelegramApiRequestException) {
                Integer errorCode = ((TelegramApiRequestException) ex.getCause()).getErrorCode();
                if (errorCode == 404) {
                    msg = "Telegram bot with provided name and token not exists";
                }
            }
            context.ui().toastr().error(msg);
            log.error(msg, ex);
            telegramEntity.setStatusError(msg);
        } finally {
            updateNotificationBlock(telegramEntity);
        }
    }

    private List<TelegramEntity.TelegramUser> checkUsers(List<TelegramEntity.TelegramUser> users) {
        if (users.isEmpty()) {
            throw new IllegalStateException("Telegram bot has no registered users");
        }
        return users;
    }

    public final class TelegramBot extends TelegramLongPollingCommandBot {

        private final BotSession botSession;
        @Getter
        private TelegramEntity telegramEntity;

        @SneakyThrows
        private TelegramBot(DefaultBotOptions botOptions, TelegramEntity telegramEntity) {
            super(botOptions);
            this.telegramEntity = telegramEntity;
            this.botSession = botsApi.registerBot(this);

            register(new TelegramStartCommand(this));
            register(new TelegramHelpCommand(this));
            register(new TelegramRegisterUserCommand(context, this));
            register(new TelegramUnregisterUserCommand(context, this));

            log.info("Registering default action'...");
            registerDefaultAction(((absSender, message) -> {

                log.warn("Telegram User {} is trying to execute unknown command '{}'.", message.getFrom().getId(),
                        message.getText());

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
                State value = new ObjectType(telegramAnswer);

                TelegramService.this.context.event()
                        .fireEvent(TELEGRAM_EVENT_PREFIX + messageId, value)
                        .fireEvent(TELEGRAM_EVENT_PREFIX + messageId + "_" + telegramAnswer.getData(), value);

                AnswerCallbackQuery query = new AnswerCallbackQuery(update.getCallbackQuery().getId());
                query.setText("Done");
                this.execute(query);

                // remove buttons
                EditMessageReplyMarkup editReplyMarkup =
                        new EditMessageReplyMarkup(Long.toString(update.getCallbackQuery().getFrom().getId()),
                                update.getCallbackQuery().getMessage().getMessageId(), null,
                                new InlineKeyboardMarkup(new ArrayList<>()));
                this.execute(editReplyMarkup);
            }
            log.debug("Income message {}", update.getMessage() != null ?
                    update.getMessage().getText() : update.getCallbackQuery().getData());
        }

        public void registerEvent(String command, String description, String workspaceId, Lock lock) {
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
