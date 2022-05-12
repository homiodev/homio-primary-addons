package org.touchhome.bundle.telegram;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.entity.types.CommunicationEntity;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.ui.UISidebarChildren;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.util.SecureString;
import org.touchhome.bundle.telegram.service.TelegramService;
import org.touchhome.common.util.CommonUtils;
import org.touchhome.common.util.Lang;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarChildren(icon = "fab fa-telegram", color = "#0088cc")
public class TelegramEntity extends CommunicationEntity<TelegramEntity> implements HasStatusAndMsg<TelegramEntity> {

    public static final String PREFIX = "telegram_";

    @UIField(order = 1, readOnly = true, hideOnEmpty = true, fullWidth = true, bg = "#334842", type = UIFieldType.HTML)
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
            return CommonUtils.OBJECT_MAPPER.readValue(users, new TypeReference<List<TelegramUser>>() {
            });
        }
        return new ArrayList<>();
    }

    @SneakyThrows
    private void setUsers(List<TelegramUser> users) {
        setJsonData("users", CommonUtils.OBJECT_MAPPER.writeValueAsString(users));
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
