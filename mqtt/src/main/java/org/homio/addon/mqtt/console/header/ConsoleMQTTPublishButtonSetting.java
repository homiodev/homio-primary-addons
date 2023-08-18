package org.homio.addon.mqtt.console.header;

import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.homio.api.model.OptionModel;
import org.homio.api.setting.SettingPluginButton;
import org.homio.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.homio.api.ui.field.action.ActionInputParameter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class ConsoleMQTTPublishButtonSetting implements ConsoleHeaderSettingPlugin<JSONObject>, SettingPluginButton {

    @Override
    public @NotNull Icon getIcon() {
        return new Icon("fas fa-upload");
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public String getConfirmMsg() {
        return null;
    }

    @Override
    public String getConfirmTitle() {
        return "MQTT.PUBLISH_TITLE";
    }

    @Override
    public List<ActionInputParameter> getInputParameters(EntityContext entityContext, String value) {
        return Arrays.asList(
                ActionInputParameter.text("Topic", "example/test"),
                ActionInputParameter.text("Content", "{value:2}"),
                ActionInputParameter.select("QoS", "0", OptionModel.list("0", "1", "2")),
                ActionInputParameter.bool("Retain", true)
        );
    }
}
