package org.homio.addon.mqtt.entity.parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.selection.UIFieldTreeNodeSelection;
import org.homio.api.ui.field.selection.dynamic.DynamicParameterFields;

@Getter
@Setter
@Accessors(chain = true)
public class MQTTTopicQueryParameter implements DynamicParameterFields {

    @UIField(order = 100, required = true)
    @UIFieldTreeNodeSelection(fileSystemIds = "MQTT", isAttachMetadata = false)
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
