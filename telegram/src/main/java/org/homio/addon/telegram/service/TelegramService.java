package org.homio.addon.telegram.service;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.telegram.TelegramEntity;
import org.homio.addon.telegram.commands.*;
import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.service.EntityService;
import org.homio.api.state.ObjectType;
import org.homio.api.state.State;
import org.homio.api.util.CommonUtils;
import org.homio.api.workspace.Lock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Log4j2
public class TelegramService extends EntityService.ServiceInstance<TelegramEntity> {

    public static final String TELEGRAM_EVENT_PREFIX = "telegram_";
    private static final Set<DynamicCommand> dynamicCommandEvents = ConcurrentHashMap.newKeySet();
    private static final Map<String, TelegramEventCommand> eventCommandMap = new HashMap<>();
    private static final @NotNull TelegramBotsApi botsApi;

    static {
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private @Nullable TelegramBot telegramBot;

    public TelegramService(@NotNull Context context, @NotNull TelegramEntity entity) {
        super(context, entity, false, "Telegram " + entity.getBotName());
    }

    public static void registerEvent(String command, String description, String id, Lock lock, @NotNull Context context) {
        dynamicCommandEvents.add(new DynamicCommand(command, description, id, lock));
        context.db().findAll(TelegramEntity.class).forEach(te -> {
            var bot = te.getService().telegramBot;
            if (bot != null) {
                bot.registerEvent(command, description, id, lock);
            }
        });
    }

    public static void unregisterEvent(String command, String id) {
        dynamicCommandEvents.remove(new DynamicCommand(command, "", id, null));
    }

    @Override
    public void destroy(boolean forRestart, @Nullable Exception ex) {
        ofNullable(telegramBot).ifPresent(TelegramBot::dispose);
    }

    @Override
    protected void initialize() {
        ofNullable(telegramBot).ifPresent(TelegramBot::dispose);
        start();
    }

    private void start() {
        try {
            if (entity.getMissingMandatoryFields().isEmpty()) {
                DefaultBotOptions botOptions = new DefaultBotOptions();
                botOptions.setProxyType(entity.getProxyType());
                botOptions.setProxyHost(entity.getProxyHost());
                botOptions.setProxyPort(entity.getProxyPort());
                botOptions.setGetUpdatesTimeout(entity.getUpdateTimeout());

                telegramBot = new TelegramBot(botOptions, entity, context);
                for (DynamicCommand dc : dynamicCommandEvents) {
                    telegramBot.registerEvent(dc.command, dc.description, dc.id, dc.lock);
                }
                log.info("Telegram bot running");
                context.ui().toastr().info("Telegram bot running");
                entity.setStatusOnline();
            } else {
                log.warn("Telegram bot not running. Requires settings.");
                context.ui().toastr().warn("Telegram bot not running. Requires settings.");
                entity.setStatus(Status.ERROR, isEmpty(entity.getBotName()) ?
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
            entity.setStatusError(msg);
        } finally {
            updateNotificationBlock();
        }
    }

    @Override
    public void entityUpdated(@NotNull TelegramEntity newEntity) {
        super.entityUpdated(newEntity);
        ofNullable(telegramBot).ifPresent(telegramBot -> telegramBot.setTelegramEntity(newEntity));
    }

    @Override
    public void updateNotificationBlock() {
        context.ui().notification().addBlockOptional("telegram", "Telegram", new Icon("fab fa-telegram", "#0088CC"));
        context.ui().notification().updateBlock("telegram", entity);
    }

    public void sendPhoto(List<TelegramEntity.TelegramUser> users, InputFile inputFile,
                          String caption) {
        if (telegramBot != null) {
            telegramBot.sendPhoto(checkUsers(users), inputFile, caption);
        }
    }

    public void sendVideo(List<TelegramEntity.TelegramUser> users, InputFile inputFile,
                          String caption) {
        if (telegramBot != null) {
            telegramBot.sendVideo(checkUsers(users), inputFile, caption);
        }
    }

    public Message sendMessage(List<TelegramEntity.TelegramUser> users, String message,
                               String[] buttons) {
        return telegramBot != null ? telegramBot.sendMessage(checkUsers(users), message, buttons) : null;
    }

    private List<TelegramEntity.TelegramUser> checkUsers(List<TelegramEntity.TelegramUser> users) {
        if (users.isEmpty()) {
            throw new IllegalStateException("Telegram bot has no registered users");
        }
        return users;
    }

    @Override
    public String isRequireRestartService() {
        if (!entity.getStatus().isOnline()) {
            return "Status: " + entity.getStatus();
        }
        if (telegramBot != null && telegramBot.botSession != null && !telegramBot.botSession.isRunning()) {
            return "Telegram service down";
        }
        return null;
    }

    @Override
    public boolean isInternetRequiredForService() {
        return true;
    }

    private record DynamicCommand(String command, String description, String id, Lock lock) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DynamicCommand that = (DynamicCommand) o;
            return Objects.equals(id, that.id) && Objects.equals(command, that.command);
        }

        @Override
        public int hashCode() {
            return Objects.hash(command, id);
        }
    }

    public static final class TelegramBot extends TelegramLongPollingCommandBot {

        private final BotSession botSession;
        private final @NotNull Context context;
        @Getter
        @Setter
        private @NotNull TelegramEntity telegramEntity;

        @SneakyThrows
        public TelegramBot(@NotNull DefaultBotOptions botOptions, @NotNull TelegramEntity telegramEntity, @NotNull Context context) {
            super(botOptions, telegramEntity.getBotToken().asString());
            this.telegramEntity = telegramEntity;
            this.botSession = botsApi.registerBot(this);
            this.context = context;

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
        public Message sendMessage(@Nullable List<TelegramEntity.TelegramUser> users,
                                   @Nullable String message,
                                   @Nullable String[] buttons) {
            if (users != null && !users.isEmpty()) {
                if (message != null) {
                    List<List<InlineKeyboardButton>> keyboard2D = new ArrayList<>();
                    if (buttons != null && buttons.length > 0) {
                        List<InlineKeyboardButton> keyboard = new ArrayList<>(buttons.length);
                        keyboard2D.add(keyboard);
                        for (String button : buttons) {
                            if (StringUtils.isNotEmpty(button)) {
                                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(button);
                                inlineKeyboardButton.setCallbackData(button);
                                keyboard.add(inlineKeyboardButton);
                            }
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

        // handle message not started with '/'
        @Override
        @SneakyThrows
        public void processNonCommandUpdate(Update update) {
            log.debug("Income message {}", update.getMessage() != null ?
                    update.getMessage().getText() : update.getCallbackQuery().getData());

            if (update.getCallbackQuery() == null) {
                String chatId = update.getMessage().getChatId().toString();
                User user = update.getMessage().getFrom();
                if (telegramEntity.getUser(user.getId()) == null) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("User not registered. Call /help first");
                    execute(message);
                } else {
                    // handle non-command message from user?
                }
                return;
            }
            if (update.getCallbackQuery() != null) {
                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                TelegramAnswer telegramAnswer = new TelegramAnswer(messageId,
                        update.getCallbackQuery().getData(),
                        update.getCallbackQuery().getFrom().getId());
                State value = new ObjectType(telegramAnswer);

                context.event()
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
