package org.homio.addon.z2m.service.properties;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.api.model.Icon;
import org.homio.api.state.DecimalType;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.json.JSONObject;

public class Z2MPropertyLastSeen extends Z2MProperty {

    public Z2MPropertyLastSeen() {
        super(new Icon("fa fa-fw fa-eye", "#2D9C2C"));
        dataReader = jsonObject -> new DecimalType(jsonObject.optNumber("last_seen", parseLastSeen(jsonObject)));
    }

    @Override
    public void buildZigbeeAction(UIInputBuilder uiInputBuilder, String entityID) {
        uiInputBuilder.addDuration(getValue().longValue(), null);
    }

    @Override
    public String getPropertyDefinition() {
        return PROPERTY_LAST_SEEN;
    }

    private Number parseLastSeen(JSONObject jsonObject) {
        Object value = jsonObject.get(PROPERTY_LAST_SEEN);
        if (value == null) {
            return System.currentTimeMillis();
        }
        try {
            TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(value.toString());
            Instant i = Instant.from(ta);
            return i.toEpochMilli();
        } catch (Exception ex) {
            return System.currentTimeMillis();
        }
    }
}
