package org.touchhome.bundle.mqtt.console.header;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.touchhome.bundle.api.ui.field.action.ActionInputParameter;

public class ConsoleMQTTPublishButtonSetting implements ConsoleHeaderSettingPlugin<JSONObject>, SettingPluginButton {

  @Override
  public String getIcon() {
    return "fas fa-upload";
  }

  @Override
  public int order() {
    return 100;
  }

  @Override
  public String getConfirmTitle() {
    return "mqtt.publish_title";
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
