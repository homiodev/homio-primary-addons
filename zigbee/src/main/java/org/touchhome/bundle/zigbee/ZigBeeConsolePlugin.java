package org.touchhome.bundle.zigbee;

import static org.touchhome.bundle.api.util.Constants.DANGER_COLOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginTable;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorBooleanMatch;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;
import org.touchhome.bundle.zigbee.setting.header.ConsoleHeaderZigBeeDiscoveryButtonSetting;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

@RequiredArgsConstructor
public class ZigBeeConsolePlugin implements ConsolePluginTable<ZigBeeConsolePlugin.ZigBeeConsoleDescription> {

  @Getter
  private final EntityContext entityContext;

  @Setter
  private ZigbeeCoordinatorEntity coordinator;

  @Override
  public int order() {
    return 500;
  }

  @Override
  public boolean isEnabled() {
    return coordinator.getStatus() == Status.ONLINE;
  }

  @Override
  public Collection<ZigBeeConsoleDescription> getValue() {
    List<ZigBeeConsoleDescription> res = new ArrayList<>();
    for (ZigBeeDeviceEntity device : coordinator.getDevices()) {
      // ZigBeeNodeDescription desc = zigBeeDevice.getZigBeeNodeDescription();
      ZigBeeNodeDescription desc = device.getZigBeeDevice().getZigBeeNodeDescription();
      res.add(new ZigBeeConsoleDescription(
          device.getName(),
          device.getIeeeAddress(),
          device.getStatus(),
          device.getStatusMessage(),
          device.getModelIdentifier(),
          desc.getFetchInfoStatus(),
          !device.getZigBeeDevice().getZigBeeConverterEndpoints().isEmpty(),
          device.getZigBeeDevice().getZigBeeNodeDescription().isNodeInitialized() &&
              !device.getZigBeeDevice().getZigBeeConverterEndpoints().isEmpty(),
          desc.getLastUpdateTime(),
          device.getEntityID()
      ));
    }
    return res;
  }

  @Override
  public Map<String, Class<? extends ConsoleHeaderSettingPlugin<?>>> getHeaderActions() {
    return Collections.singletonMap("zigbee.start_discovery", ConsoleHeaderZigBeeDiscoveryButtonSetting.class);
  }

  @Override
  public Class<ZigBeeConsoleDescription> getEntityClass() {
    return ZigBeeConsoleDescription.class;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ZigBeeConsoleDescription implements HasEntityIdentifier {

    @UIField(order = 1, inlineEdit = true)
    private String name;

    @UIField(order = 1)
    private String ieeeAddress;

    @UIField(order = 2)
    @UIFieldColorStatusMatch
    private Status deviceStatus;

    @UIField(order = 3, color = DANGER_COLOR)
    private String errorMessage;

    @UIField(order = 4)
    @UIFieldSelection(SelectModelIdentifierDynamicLoader.class)
    @UIFieldSelectValueOnEmpty(label = "zigbee.action.selectModelIdentifier", color = "#A7D21E")
    private String model;

    @UIField(order = 5)
    private ZigBeeNodeDescription.FetchInfoStatus fetchInfoStatus;

    @UIField(order = 6)
    @UIFieldColorBooleanMatch
    private boolean channelsInitialized;

    @UIField(order = 7)
    @UIFieldColorBooleanMatch
    private boolean initialized;

    @UIField(order = 8)
    private Date lastUpdate;

    private String entityID;

    @UIContextMenuAction("ACTION.INITIALIZE_ZIGBEE_NODE")
    public ActionResponseModel initializeZigBeeNode(ZigBeeDeviceEntity zigBeeDeviceEntity) {
      return zigBeeDeviceEntity.initializeZigBeeNode();
    }

    @UIContextMenuAction("ACTION.SHOW_NODE_DESCRIPTION")
    public ActionResponseModel showNodeDescription(ZigBeeDeviceEntity zigBeeDeviceEntity) {
      return ActionResponseModel.showJson("Zigbee node description", zigBeeDeviceEntity.getZigBeeNodeDescription());
    }

    @UIContextMenuAction("ACTION.SHOW_LAST_VALUES")
    public ActionResponseModel showLastValues(ZigBeeDeviceEntity zigBeeDeviceEntity,
        ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener) {
      return zigBeeDeviceEntity.showLastValues(zigBeeDeviceEntity, zigBeeDeviceUpdateValueListener);
    }

    @UIContextMenuAction("ACTION.REDISCOVERY")
    public ActionResponseModel rediscoveryNode(ZigBeeDeviceEntity zigBeeDeviceEntity) {
      return zigBeeDeviceEntity.rediscoveryNode();
    }

    @UIContextMenuAction("ACTION.PERMIT_JOIN")
    public ActionResponseModel permitJoin(ZigBeeDeviceEntity zigBeeDeviceEntity, EntityContext entityContext) {
      return zigBeeDeviceEntity.permitJoin();
    }

    @UIContextMenuAction("ACTION.ZIGBEE_PULL_CHANNELS")
    public ActionResponseModel pullChannels(ZigBeeDeviceEntity zigBeeDeviceEntity) {
      return zigBeeDeviceEntity.pullChannels();
    }
  }
}
