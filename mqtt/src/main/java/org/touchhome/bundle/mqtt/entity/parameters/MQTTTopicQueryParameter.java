package org.touchhome.bundle.mqtt.entity.parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.selection.UIFieldTreeNodeSelection;
import org.touchhome.bundle.api.ui.field.selection.dynamic.DynamicParameterFields;

@Getter
@Setter
@Accessors(chain = true)
public class MQTTTopicQueryParameter implements DynamicParameterFields {

  @UIField(order = 100, required = true)
  @UIFieldTreeNodeSelection(fileSystemIds = "MQTT")
  public String queryTopic;

  @Override
  public String getGroupName() {
    return "MQTT topic";
  }

  @Override
  public String getBorderColor() {
    return "#0E7EBC";
  }
}
