package org.touchhome.bundle.zigbee.service.z2m;

import static java.lang.String.format;

import javax.measure.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.ObjectType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;

@Getter
@RequiredArgsConstructor
public abstract class Z2MProperty {

  private final @NotNull String property;
  private final @NotNull String color;
  private final @NotNull String icon;
  private final @Nullable Unit<?> unit;

  private String entityID;
  private Z2MDeviceService deviceService;
  private Options expose;
  private State value = new StringType("N/A");
  private String variableId;
  private long updated;

  public void init(Z2MDeviceService deviceHandler, Options expose) {
    this.deviceService = deviceHandler;
    this.expose = expose;
    this.entityID = deviceHandler.getDevice().getIeeeAddress() + "_" + property;
  }

  public void mqttUpdate(JSONObject payload) {
    this.updated = System.currentTimeMillis();
    value = readValue(payload);

    EntityContext entityContext = deviceService.getCoordinatorService().getEntityContext();
    pushVariable(entityContext);

    ObjectType entityUpdated = new ObjectType(this);
    // entityContext.event().fireEvent(deviceHandler.getDevice().getIeeeAddress(), entityUpdated);
    entityContext.event().fireEvent(entityID, entityUpdated);
  }

  protected State readValue(JSONObject payload) {
    if (unit != null) {
      return new QuantityType<>(payload.getNumber(getProperty()).intValue(), unit);
    } else {
      return new DecimalType(payload.getNumber(getProperty()).intValue());
    }
  }

  protected void pushVariable(EntityContext entityContext) {
    if (variableId == null) {
      variableId = entityContext.var().createVariable(deviceService.getDeviceEntity().getEntityID(),
          entityID, format("${%s:%s}", getName(), expose.getName()), getVariableType(),
          getVariableDescription(), color);
    }
    entityContext.var().set(variableId, value);
  }

  private String getVariableDescription() {
    String extra = "";
    if (expose.getValueMin() != null && expose.getValueMax() != null) {
      extra = format("(range:%s...%s)", expose.getValueMin(), expose.getValueMax());
    } else if (expose.getValueOn() != null && expose.getValueOff() != null) {
      extra = format("(on:%s;off:%s)", expose.getValueOn(), expose.getValueOff());
    }
    return format("EP[%s%s%s] ${%s} [%s]", property, StringUtils.defaultString(expose.getUnit(), ""), extra,
        deviceService.getDeviceEntity().getName(), deviceService.getDevice().getIeeeAddress());
  }

  private VariableType getVariableType() {
    switch (expose.getType()) {
      case "numeric":
        return VariableType.Float;
      case "binary":
      case "switch":
        return VariableType.Boolean;
      default:
        return VariableType.Any;
    }
  }

  public String getName() {
    return "zigbee.endpoint.name." + property;
  }

  public String getDescription() {
    return "zigbee.endpoint.description." + property;
  }
}
