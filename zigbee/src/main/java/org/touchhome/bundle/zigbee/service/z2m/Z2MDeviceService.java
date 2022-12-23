package org.touchhome.bundle.zigbee.service.z2m;

import static java.lang.String.format;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.ui.UI;
import org.touchhome.bundle.zigbee.model.z2m.Z2MDeviceEntity;
import org.touchhome.bundle.zigbee.service.z2m.properties.Z2MPropertyLastUpdate;
import org.touchhome.bundle.zigbee.service.z2m.properties.Z2MPropertyUnknown;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.touchhome.bundle.zigbee.util.ZigBeeUtil;
import org.touchhome.common.util.CommonUtils;
import org.touchhome.common.util.Lang;

@Getter
@Log4j2
public class Z2MDeviceService {

  private final Set<String> skipProperties = Set.of("illuminance_lux");
  private final Z2MLocalCoordinatorService coordinatorService;
  private final Map<String, Z2MProperty> properties = new HashMap<>();
  private final Z2MDeviceEntity deviceEntity;
  private final EntityContext entityContext;
  private Z2MDeviceDTO device;

  public Z2MDeviceService(Z2MLocalCoordinatorService coordinatorService, Z2MDeviceDTO device) {
    this.coordinatorService = coordinatorService;
    this.entityContext = coordinatorService.getEntityContext();
    this.deviceUpdated(device);
    this.deviceEntity = new Z2MDeviceEntity(this);

    String groupName = format("${%s} [${PLACE_NOT_SET}]", this.device.getName());
    entityContext.var().createGroup("zigbee", this.deviceEntity.getEntityID(),
        groupName, true, ZigBeeUtil.getDeviceIcon(this.device.getModelId(), "fas fa-server"),
        ZigBeeUtil.getDeviceIconColor(this.device.getModelId(), UI.Color.random()), this.device.getGroupDescription());
    entityContext.ui().updateItem(deviceEntity);
    entityContext.ui().sendSuccessMessage(Lang.getServerMessage("ENTITY_CREATED", "NAME", format("${%s}", this.device.getName())));
    entityContext.event().addEventBehaviourListener(getDeviceTopic(this.device),
        payload -> mqttUpdate(new JSONObject(payload.toString())));
  }

  public String getDeviceTopic(Z2MDeviceDTO device) {
    return format("%s-%s/%s", coordinatorService.getMqttEntity().getEntityID(), coordinatorService.getEntity().getBasicTopic(), device.getIeeeAddress());
  }

  private void mqttUpdate(JSONObject payload) {
    for (String key : payload.keySet()) {
      Z2MProperty z2MProperty = properties.get(key);
      if (z2MProperty != null) {
        z2MProperty.mqttUpdate(payload);
      } else if (!skipProperties.contains(key)) {
        log.warn("[{}]: Unable to find zigbee2mqtt handler for mqtt property '{}'", coordinatorService.getEntityID(), key);
      }
    }
  }

  public void dispose() {
    entityContext.event().removeEvents(getDeviceTopic(device));
    entityContext.ui().removeItem(deviceEntity);
    entityContext.ui().sendSuccessMessage(Lang.getServerMessage("ENTITY_DELETED", "NAME", format("${%s}", device.getName())));
  }

  public void deviceUpdated(Z2MDeviceDTO device) {
    this.device = device;
    for (Options expose : device.getDefinition().getExposes()) {
      if (!skipProperties.contains(expose.getProperty())) {
        if (!properties.containsKey(expose.getProperty())) {
          Class<? extends Z2MProperty> z2mCluster = coordinatorService.getZ2mConverters().get(expose.getProperty());
          Z2MProperty z2MProperty = z2mCluster == null ? new Z2MPropertyUnknown(expose.getProperty()) : CommonUtils.newInstance(z2mCluster);
          z2MProperty.init(this, expose);
          properties.put(expose.getProperty(), z2MProperty);
        }
        if (!properties.containsKey("updated")) {
          Z2MProperty z2MProperty = new Z2MPropertyLastUpdate();
          z2MProperty.init(this, expose);
          properties.put("updated", z2MProperty);
        }
      }
    }
  }

  @Override
  public String toString() {
    return device.toString();
  }

  public JsonNode getConfiguration() {
    return coordinatorService.getConfiguration().getDevices().getOrDefault(device.getIeeeAddress(), OBJECT_MAPPER.createObjectNode());
  }

  public void updateConfiguration(String key, Object value) {
    this.coordinatorService.updateDeviceConfiguration(this, key, value);
  }
}
