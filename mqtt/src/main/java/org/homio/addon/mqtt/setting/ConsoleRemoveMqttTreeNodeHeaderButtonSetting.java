package org.homio.addon.mqtt.setting;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.mqtt.setting.ConsoleRemoveMqttTreeNodeHeaderButtonSetting.NodeRemoveRequest;
import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.homio.api.setting.SettingPlugin;
import org.homio.api.setting.SettingType;
import org.homio.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.homio.api.ui.UI.Color;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static org.homio.api.util.JsonUtils.OBJECT_MAPPER;
import static org.homio.api.util.JsonUtils.putOpt;

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
    public @NotNull JSONObject getParameters(Context context, String value) {
        JSONObject parameters = new JSONObject();
        putOpt(parameters, "confirm", getConfirmMsg());
        putOpt(parameters, "dialogColor", Color.ERROR_DIALOG);
        putOpt(parameters, "confirmInjectSelectedNode", true);
        putOpt(parameters, "title", null);
        return parameters;
    }

    @Override
    @SneakyThrows
    public NodeRemoveRequest deserializeValue(Context context, String value) {
        return StringUtils.isEmpty(value) ? null : OBJECT_MAPPER.readValue(value, NodeRemoveRequest.class);
    }

    @Override
    @SneakyThrows
    public @NotNull String serializeValue(NodeRemoveRequest value) {
        return value == null ? "" : OBJECT_MAPPER.writeValueAsString(value);
    }

    @Getter
    @Setter
    public static class NodeRemoveRequest {

        private String tabID;
        private String nodeID;
    }
}
