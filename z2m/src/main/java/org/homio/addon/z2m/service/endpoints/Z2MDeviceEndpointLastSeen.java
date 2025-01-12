package org.homio.addon.z2m.service.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.homio.api.state.DecimalType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class Z2MDeviceEndpointLastSeen extends Z2MDeviceEndpoint {

  public Z2MDeviceEndpointLastSeen(@NotNull Context context) {
    super(new Icon("fa fa-eye", "#2D9C2C"), context);
    setDataReader(payload -> new DecimalType(parseLastSeen(payload)));
  }

  @Override
  public String getEndpointDefinition() {
    return ENDPOINT_LAST_SEEN;
  }

  private Long parseLastSeen(JsonNode payload) {
    if (!payload.has(ENDPOINT_LAST_SEEN)) {
      return System.currentTimeMillis();
    }
    // long or string
    JsonNode value = payload.get(ENDPOINT_LAST_SEEN);
    if (value.isNumber()) {
      return value.asLong();
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
