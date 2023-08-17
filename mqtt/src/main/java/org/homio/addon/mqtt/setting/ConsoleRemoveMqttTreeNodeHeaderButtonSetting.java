package org.homio.addon.mqtt.setting;

import static org.homio.api.util.JsonUtils.OBJECT_MAPPER;
import static org.homio.api.util.JsonUtils.putOpt;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.mqtt.setting.ConsoleRemoveMqttTreeNodeHeaderButtonSetting.NodeRemoveRequest;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.homio.api.setting.SettingPlugin;
import org.homio.api.setting.SettingType;
import org.homio.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.homio.api.ui.UI.Color;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * 'Remove button' console header button for tree/table console blocks.
 */
public class ConsoleRemoveMqttTreeNodeHeaderButtonSetting implements
    ConsoleHeaderSettingPlugin<NodeRemoveRequest>,
    SettingPlugin<NodeRemoveRequest> {

    @Override
    public Icon getIcon() {
        return new Icon("fas fa-trash");
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public @NotNull Class<NodeRemoveRequest> getType() {
        return ConsoleRemoveMqttTreeNodeHeaderButtonSetting.NodeRemoveRequest.class;
    }

    @Override
    public @NotNull SettingType getSettingType() {
        return SettingType.Button;
    }

    public String getConfirmMsg() {
        return "W.CONFIRM.MQTT_DELETE";
    }

    @Override
    public JSONObject getParameters(EntityContext entityContext, String value) {
        JSONObject parameters = new JSONObject();
        putOpt(parameters, "confirm", getConfirmMsg());
        putOpt(parameters, "dialogColor", Color.ERROR_DIALOG);
        putOpt(parameters, "confirmInjectSelectedNode", true);
        putOpt(parameters, "title", null);
        return parameters;
    }

    @Override
    @SneakyThrows
    public NodeRemoveRequest parseValue(EntityContext entityContext, String value) {
        return StringUtils.isEmpty(value) ? null : OBJECT_MAPPER.readValue(value, NodeRemoveRequest.class);
    }

    @Override
    @SneakyThrows
    public @NotNull String writeValue(NodeRemoveRequest value) {
        return value == null ? "" : OBJECT_MAPPER.writeValueAsString(value);
    }

    @Getter
    @Setter
    public static class NodeRemoveRequest {

        private String tabID;
        private String nodeID;
    }
}
