package org.homio.addon.z2m.service.properties;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.properties.dynamic.Z2MDynamicProperty;
import org.homio.addon.z2m.util.Z2MDeviceModel;
import org.homio.addon.z2m.util.Z2MDeviceModel.Z2MDeviceDefinition.Options;
import org.homio.api.state.DecimalType;
import org.json.JSONObject;

public class Z2MPropertyLastSeen extends Z2MDynamicProperty {

    public static final String LAST_SEEN = "last_seen";

    public Z2MPropertyLastSeen(Z2MDeviceService deviceService) {
        super("#2D9C2C", "fa fa-fw fa-eye");
        setValue(new DecimalType(System.currentTimeMillis()));
        init(deviceService, Options.dynamicExpose(LAST_SEEN, Z2MDeviceModel.NUMBER_TYPE));
        dataReader = jsonObject -> {
            return new DecimalType(jsonObject.optNumber("last_seen", parseLastSeen(jsonObject)));
        };
    }

    private Number parseLastSeen(JSONObject jsonObject) {
        Object value = jsonObject.get(LAST_SEEN);
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
