package org.touchhome.bundle.telegram;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.entity.MessengerEntity;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.UISidebarChildren;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldRenderAsHTML;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.util.SecureString;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.telegram.service.TelegramService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarChildren(icon = "fab fa-telegram", color = "#0088cc")
public class TelegramEntity extends MessengerEntity<TelegramEntity> implements HasStatusAndMsg<TelegramEntity> {

    public static final String PREFIX = "telegram_";

    @Getter
    @UIField(order = 22, readOnly = true, hideOnEmpty = true)
    @UIFieldColorStatusMatch
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Status status;

    @Getter
    @UIField(order = 23, readOnly = true, hideOnEmpty = true)
    @Column(length = 512)
    private String statusMessage;

    @UIField(order = 1, required = true, readOnly = true, hideOnEmpty = true, fullWidth = true, bg = "#334842")
    @UIFieldRenderAsHTML
    public String getDescription() {
        if (StringUtils.isEmpty(getBotName()) || StringUtils.isEmpty(getBotToken())) {
            return Lang.getServerMessage("telegram.description");
        }
        return null;
    }

    @UIField(order = 30, required = true, inlineEditWhenEmpty = true)
    public String getBotName() {
        return getJsonData("botName");
    }

    public TelegramEntity setBotName(String value) {
        return setJsonData("botName", value);
    }

    @UIField(order = 40, required = true, inlineEditWhenEmpty = true)
    public SecureString getBotToken() {
        return new SecureString(getJsonData("botToken"));
    }

    public TelegramEntity setBotToken(String value) {
        return setJsonData("botToken", value);
    }

    @UIField(order = 50, readOnly = true, type = UIFieldType.Chips, label = "users")
    public List<String> getRegisteredUsers() {
        return getUsers().stream().map(TelegramUser::getName).collect(Collectors.toList());
    }

    @JsonIgnore
    @SneakyThrows
    public List<TelegramUser> getUsers() {
        String users = getJsonData("users");
        if (StringUtils.isNotEmpty(users)) {
            return TouchHomeUtils.OBJECT_MAPPER.readValue(users, new TypeReference<List<TelegramUser>>() {
            });
        }
        return new ArrayList<>();
    }

    @SneakyThrows
    private void setUsers(List<TelegramUser> users) {
        setJsonData("users", TouchHomeUtils.OBJECT_MAPPER.writeValueAsString(users));
    }

    @Override
    public String getDefaultName() {
        return "TeleBot";
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }

    @UIContextMenuAction(value = "RESTART", icon = "fas fa-power-off")
    public ActionResponseModel reboot(EntityContext entityContext) {
        entityContext.getBean(TelegramService.class).restart(this);
        return ActionResponseModel.showSuccess("SUCCESS");
    }

    @Override
    public void afterDelete(EntityContext entityContext) {
        entityContext.getBean(TelegramService.class).dispose(this);
    }

    @Override
    public void afterUpdate(EntityContext entityContext) {
        entityContext.getBean(TelegramService.class).setTelegramEntity(this);
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