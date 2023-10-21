package org.homio.addon.z2m.service.endpoints;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.homio.api.state.DecimalType;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MDeviceEndpointLastSeen extends Z2MDeviceEndpoint {

    public Z2MDeviceEndpointLastSeen(@NotNull Context context) {
        super(new Icon("fa fa-eye", "#2D9C2C"), context);
        setInitialValue(new DecimalType(System.currentTimeMillis()));
        setDataReader(jsonObject -> new DecimalType(parseLastSeen(jsonObject)));
    }

    @Override
    public void assembleUIAction(UIInputBuilder uiInputBuilder) {
        uiInputBuilder.addDuration(getValue().longValue(), null);
    }

    @Override
    public String getEndpointDefinition() {
        return ENDPOINT_LAST_SEEN;
    }

    private Number parseLastSeen(JSONObject jsonObject) {
        if (jsonObject == null || !jsonObject.has(ENDPOINT_LAST_SEEN)) {
            return System.currentTimeMillis();
        }
        // long or string
        Object value = jsonObject.get(ENDPOINT_LAST_SEEN);
        if (value instanceof Number num) {
            return num;
        }
        // try parse from string
        try {
            TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(value.toString());
            Instant i = Instant.from(ta);
            return i.toEpochMilli();
        } catch (Exception ex) {
            return System.currentTimeMillis();
        }
    }
}
