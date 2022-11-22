package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.ZigBeeNode.ZigBeeNodeState;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor.LogicalType;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor.CurrentPowerModeType;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor.PowerLevelType;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor.PowerSourceType;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContextSetting;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorMatch;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;

/**
 * Interface hold fields that common for coordinator and end devices
 */
public interface HasNodeDescriptor extends HasJsonData, HasEntityIdentifier {

  @UIField(order = 11, hideInEdit = true)
  @UIFieldColorStatusMatch
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  default Status getNodeState() {
    return EntityContextSetting.getStatus(this, "node", Status.UNKNOWN);
  }

  default void setNodeState(ZigBeeNodeState value) {
    Status status = value == null || value == ZigBeeNodeState.UNKNOWN ?
        Status.UNKNOWN : value == ZigBeeNodeState.ONLINE ? Status.ONLINE : Status.OFFLINE;
    if (getFetchInfoStatus() != status) {
      EntityContextSetting.setStatus(this, "node", "NodeState", status);
    }
  }

  @UIField(order = 12, hideInEdit = true)
  @UIFieldColorStatusMatch
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  default Status getFetchInfoStatus() {
    return EntityContextSetting.getStatus(this, "fetch_info", Status.UNKNOWN);
  }

  default void setFetchInfoStatus(Status status, @Nullable String msg) {
    EntityContextSetting.setStatus(this, "fetch_info", "FetchInfoStatus", status, msg);
  }

  @UIField(order = 1, hideInEdit = true)
  @UIFieldGroup(value = "Node", order = 25, borderColor = "#44B377")
  default int getNetworkAddress() {
    return getJsonData("na", 0);
  }

  default void setNetworkAddress(int value) {
    setJsonData("na", value);
  }

  @UIField(order = 5, hideInEdit = true)
  @UIFieldGroup("Node")
  default LogicalType getLogicalType() {
    return getJsonDataEnum("lt", LogicalType.UNKNOWN);
  }

  default void setLogicalType(LogicalType value) {
    setJsonDataEnum("lt", value);
  }

  @UIField(hideInEdit = true, order = 6, hideOnEmpty = true)
  @UIFieldGroup("Node")
  default int getManufacturerCode() {
    return getJsonData("mc", 0);
  }

  default void setManufacturerCode(int value) {
    setJsonData("mc", value);
  }

  @UIField(hideInEdit = true, order = 7, hideOnEmpty = true)
  @UIFieldGroup("Node")
  default long getNodeLastUpdateTime() {
    return getJsonData("lu", 0L);
  }

  default void setNodeLastUpdateTime(long value) {
    setJsonData("lu", value);
  }

  @UIField(hideInEdit = true, order = 8, hideOnEmpty = true)
  @UIFieldGroup("Node")
  default String getFirmwareVersion() {
    return getJsonData("fw");
  }

  default void setFirmwareVersion(String value) {
    setJsonData("fw", value);
  }

  @UIField(hideInEdit = true, order = 1, hideOnEmpty = true)
  @UIFieldGroup(value = "Power", order = 30, borderColor = "#5F5CA1")
  @UIFieldColorMatch(value = "CRITICAL", color = "#DB4318")
  @UIFieldColorMatch(value = "LOW", color = "#D0DB18")
  @UIFieldColorMatch(value = "MEDIUM", color = "#97DB18")
  @UIFieldColorMatch(value = "FULL", color = "#4DDB18")
  @UIFieldColorMatch(value = "UNKNOWN", color = "#818744")
  default PowerLevelType getCurrentPowerLevel() {
    return getJsonDataEnum("pw", PowerLevelType.UNKNOWN);
  }

  default void setCurrentPowerLevel(PowerLevelType powerLevel) {
    setJsonDataEnum("pw", powerLevel);
  }

  @UIField(hideInEdit = true, order = 2)
  @UIFieldGroup("Power")
  default CurrentPowerModeType getCurrentPowerMode() {
    return getJsonDataEnum("pm", CurrentPowerModeType.UNKNOWN);
  }

  default void setCurrentPowerMode(CurrentPowerModeType value) {
    setJsonDataEnum("pm", value);
  }

  @UIField(hideInEdit = true, order = 3, hideOnEmpty = true)
  @UIFieldGroup("Power")
  default PowerSourceType getCurrentPowerSource() {
    return getJsonDataEnum("ps", PowerSourceType.UNKNOWN);
  }

  default void setCurrentPowerSource(PowerSourceType value) {
    setJsonDataEnum("ps", value);
  }

  @UIField(hideInEdit = true, type = UIFieldType.Chips, order = 4, hideOnEmpty = true)
  @UIFieldGroup("Power")
  default Set<String> getAvailablePowerSources() {
    return getJsonDataSet("aps");
  }

  default void setAvailablePowerSources(String value) {
    setJsonData("aps", value);
  }

  default boolean updateFromNodeDescriptor(ZigBeeNode node) {
    boolean updated = false;

    setNodeState(node.getNodeState());
    if (!Objects.equals(getNetworkAddress(), node.getNetworkAddress())) {
      setNetworkAddress(node.getNetworkAddress());
      updated = true;
    }
    PowerDescriptor pd = node.getPowerDescriptor();
    if (pd != null) {
      if (!Objects.equals(getCurrentPowerLevel(), pd.getPowerLevel())) {
        setCurrentPowerLevel(pd.getPowerLevel());
        updated = true;
      }
      if (!Objects.equals(getCurrentPowerMode(), pd.getCurrentPowerMode())) {
        setCurrentPowerMode(pd.getCurrentPowerMode());
        updated = true;
      }
      if (!Objects.equals(getCurrentPowerSource(), pd.getCurrentPowerSource())) {
        setCurrentPowerSource(pd.getCurrentPowerSource());
        updated = true;
      }
      Set<String> availablePowerSources = pd.getAvailablePowerSources().stream().map(Enum::name).collect(Collectors.toSet());
      if (!Objects.equals(getAvailablePowerSources(), availablePowerSources)) {
        setAvailablePowerSources(String.join("~~~", availablePowerSources));
        updated = true;
      }
    }
    NodeDescriptor nd = node.getNodeDescriptor();
    if (nd != null) {
      if (!Objects.equals(getManufacturerCode(), nd.getManufacturerCode())) {
        setManufacturerCode(nd.getManufacturerCode());
        updated = true;
      }
      if (!Objects.equals(getLogicalType(), nd.getLogicalType())) {
        setLogicalType(nd.getLogicalType());
        updated = true;
      }
    }
    if (node.getLastUpdateTime() != null) {
      if (getNodeLastUpdateTime() != node.getLastUpdateTime().getTime()) {
        setNodeLastUpdateTime(node.getLastUpdateTime().getTime());
        updated = true;
      }
    }
    return updated;
  }
}
