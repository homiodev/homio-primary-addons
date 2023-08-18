package org.homio.addon.mqtt.entity.parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.selection.UIFieldStaticSelection;
import org.homio.api.ui.field.selection.dynamic.DynamicParameterFields;

@Getter
@Setter
@Accessors(chain = true)
public class MQTTPublishQueryParameter implements DynamicParameterFields {

    @UIField(order = 100, required = true)
    public String publishTopic;

    @UIField(order = 120)
    @UIFieldStaticSelection(value = {"0", "1", "2"})
    public int qos = 0;

    @UIField(order = 150)
    public boolean retain;

    @Override
    public String getGroupName() {
        return "MQTT topic";
    }

    @Override
    public String getBorderColor() {
        return "#0E7EBC";
    }
}
