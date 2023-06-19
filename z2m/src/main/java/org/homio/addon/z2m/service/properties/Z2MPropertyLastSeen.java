package org.homio.addon.z2m.service.properties;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.model.Icon;
import org.homio.api.state.DecimalType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MPropertyLastSeen extends Z2MProperty {

    public Z2MPropertyLastSeen(Z2MDeviceService deviceService) {
        super(new Icon("fa fa-fw fa-eye", "#2D9C2C"));
        setValue(new DecimalType(System.currentTimeMillis()));
        init(deviceService, Options.dynamicExpose(PROPERTY_LAST_SEEN, ApplianceModel.NUMBER_TYPE));
        dataReader = jsonObject -> new DecimalType(jsonObject.optNumber("last_seen", parseLastSeen(jsonObject)));
    }

    @Override
    public @NotNull String getPropertyDefinition() {
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
