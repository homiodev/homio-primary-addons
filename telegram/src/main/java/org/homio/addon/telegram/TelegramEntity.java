package org.homio.addon.telegram;

import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.telegram.service.TelegramService;
import org.homio.api.EntityContext;
import org.homio.api.entity.HasStatusAndMsg;
import org.homio.api.entity.types.CommunicationEntity;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.Icon;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.UISidebarChildren;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldGroup;
import org.homio.api.ui.field.UIFieldSlider;
import org.homio.api.ui.field.UIFieldType;
import org.homio.api.ui.field.action.UIContextMenuAction;
import org.homio.api.util.Lang;
import org.homio.api.util.SecureString;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;
import org.telegram.telegrambots.meta.ApiConstants;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarChildren(icon = "fab fa-telegram", color = "#0088CC")
public final class TelegramEntity extends CommunicationEntity<TelegramEntity> implements HasStatusAndMsg<TelegramEntity> {

    @UIField(order = 1, hideInEdit = true, hideOnEmpty = true, fullWidth = true, bg = "#334842C2", type = UIFieldType.HTML)
    public String getDescription() {
        if (StringUtils.isEmpty(getBotName()) || StringUtils.isEmpty(getBotToken())) {
            return Lang.getServerMessage("telegram.description");
        }
        return null;
    }

    @Override
    public @NotNull String getTitle() {
        return StringUtils.defaultIfEmpty(getBotName(), "telegram.no_bot_name");
    }

    @UIField(order = 30, required = true, inlineEditWhenEmpty = true)
    public String getBotName() {
        return getJsonData("botName");
    }

    public void setBotName(String value) {
        setJsonData("botName", value);
    }

    @UIField(order = 40, required = true, inlineEditWhenEmpty = true)
    public SecureString getBotToken() {
        return new SecureString(getJsonData("botToken"));
    }

    public void setBotToken(String value) {
        setJsonData("botToken", value);
    }

    @UIField(order = 45)
    @UIFieldSlider(max = 360, min = 5, step = 5)
    public int getWaitQuestionMaxSeconds() {
        return getJsonData("wqms", 60);
    }

    public void setWaitQuestionMaxSeconds(int value) {
        setJsonData("wqms", value);
    }

    @UIField(order = 1, hideInView = true)
    @UIFieldGroup(value = "PROXY", order = 20, borderColor = "#8C324C")
    public ProxyType getProxyType() {
        return getJsonDataEnum("pt", ProxyType.NO_PROXY);
    }

    public void setProxyType(ProxyType value) {
        setJsonDataEnum("pt", value);
    }

    @UIField(order = 2, hideInView = true)
    @UIFieldGroup("PROXY")
    public String getProxyHost() {
        return getJsonData("ph");
    }

    public void setProxyHost(String value) {
        setJsonData("ph", value);
    }

    @UIField(order = 3, hideInView = true)
    @UIFieldGroup("PROXY")
    public int getProxyPort() {
        return getJsonData("pp", 0);
    }

    public void setProxyPort(int value) {
        setJsonData("pp", value);
    }

    @UIField(order = 65, isRevert = true, label = "getUpdatesTimeout")
    @UIFieldSlider(min = 10, max = 120)
    public int getUpdateTimeout() {
        return getJsonData("ut", ApiConstants.GETUPDATES_TIMEOUT);
    }

    public void setUpdateTimeout(int value) {
        setJsonData("ut", value);
    }

    @UIField(order = 100, hideInEdit = true, type = UIFieldType.Chips, label = "users")
    public List<String> getRegisteredUsers() {
        return getUsers().stream().map(TelegramUser::getName).collect(Collectors.toList());
    }

    @JsonIgnore
    @SneakyThrows
    public List<TelegramUser> getUsers() {
        String users = getJsonData("users");
        if (StringUtils.isNotEmpty(users)) {
            return OBJECT_MAPPER.readValue(users, new TypeReference<List<TelegramUser>>() {
            });
        }
        return new ArrayList<>();
    }

    @SneakyThrows
    private void setUsers(List<TelegramUser> users) {
        setJsonData("users", OBJECT_MAPPER.writeValueAsString(users));
    }

    @Override
    public String getDefaultName() {
        return "TeleBot";
    }

    @Override
    public @NotNull Icon getEntityIcon() {
        return new Icon("fas fa-robot");
    }

    @Override
    protected @NotNull String getDevicePrefix() {
        return "telegram";
    }

    @UIContextMenuAction(value = "RESTART", icon = "fas fa-power-off", iconColor = Color.RED)
    public ActionResponseModel reboot(EntityContext entityContext) {
        entityContext.getBean(TelegramService.class).restart(this);
        return ActionResponseModel.showSuccess("SUCCESS");
    }

    @Override
    public void afterDelete(EntityContext entityContext) {
        entityContext.getBean(TelegramService.class).dispose(this);
    }

    @Override
    public void afterUpdate(EntityContext entityContext, boolean persist) {
        entityContext.getBean(TelegramService.class).entityUpdated(this);
    }

    public TelegramUser getUser(long id) {
        return getUsers().stream().filter(u -> u.id == id).findAny().orElse(null);
    }

    public void removeUser(Long id) {
        setUsers(getUsers().stream().filter(u -> u.id != id).collect(Collectors.toList()));
    }

    public void addUser(Long id, String name, String lastName, String chatId) {
        List<TelegramUser> users = getUsers();
        users.add(new TelegramUser(id, name, lastName, chatId));
        setUsers(users);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelegramUser {

        private long id;
        private String name;
        private String lastName;
        private String chatId;
    }
}
