package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zsmartsystems.zigbee.ExtendedPanId;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.security.ZigBeeKey;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.types.MicroControllerBaseEntity;
import org.touchhome.bundle.api.exception.ProhibitedExecution;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.ui.UIEntityLog;
import org.touchhome.bundle.api.ui.UISidebarChildren;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldShowOnCondition;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.selection.UIFieldDevicePortSelection;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectNoValue;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldStaticSelection;
import org.touchhome.bundle.api.ui.field.selection.UIFieldTreeNodeSelection;
import org.touchhome.bundle.zigbee.handler.CC2531Service;
import org.touchhome.bundle.zigbee.model.service.ZigBeeCoordinatorService;

@Log4j2
@Entity
@UISidebarChildren(icon = "fas fa-circle-nodes", color = "#D46A06")
@UIEntityLog(topic = ZigbeeCoordinatorEntity.class)
@UIEntityLog(topic = ZigBeeCoordinatorService.class)
public final class ZigbeeCoordinatorEntity extends MicroControllerBaseEntity<ZigbeeCoordinatorEntity>
    implements HasNodeDescriptor, EntityService<ZigBeeCoordinatorService, ZigbeeCoordinatorEntity> {

  /**
   * Default ZigBeeAlliance09 link key
   */
  public final static ZigBeeKey KEY_ZIGBEE_ALLIANCE_O9 = new ZigBeeKey(new int[]{0x5A, 0x69, 0x67, 0x42, 0x65,
      0x65, 0x41, 0x6C, 0x6C, 0x69, 0x61, 0x6E, 0x63, 0x65, 0x30, 0x39});

  public static final String PREFIX = "zbc_";

  @Override
  @UIFieldIgnore
  @JsonIgnore
  public String getPlace() {
    throw new ProhibitedExecution();
  }

  @Getter
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "coordinatorEntity", cascade = CascadeType.REMOVE)
  private Set<ZigBeeDeviceEntity> devices;

  @Override
  public String getDefaultName() {
    return "ZigBee";
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  @UIField(order = 1, inlineEdit = true)
  @UIFieldGroup(value = "General", order = 1)
  public boolean isStart() {
    return getJsonData("start", false);
  }

  @UIField(order = 2, inlineEdit = true)
  @UIFieldGroup("General")
  public boolean isLogEvents() {
    return getJsonData("le", false);
  }

  @UIField(order = 4)
  @UIFieldGroup("General")
  public ZigbeeCoordinator getCoordinatorHandler() {
    return getJsonDataEnum("ch", ZigbeeCoordinator.CC2531Handler);
  }

  @UIField(order = 3, required = true)
  @UIFieldDevicePortSelection
  @UIFieldSelectValueOnEmpty(label = "selection.selectPort", icon = "fas fa-door-open")
  @UIFieldSelectNoValue("error.noPortsAvailable")
  @UIFieldGroup(value = "Port", order = 5, borderColor = "#29A397")
  public String getPort() {
    return getJsonData("port", "");
  }

  @UIField(order = 180, advanced = true)
  @UIFieldStaticSelection(value = {"38400", "57600", "115200"})
  @UIFieldGroup("Port")
  public int getPortBaud() {
    return getJsonData("pb", 115200);
  }

  @UIField(order = 220, advanced = true)
  @UIFieldStaticSelection({"0:None", "1:Hardware (CTS/RTS)", "2:Software (XOn/XOff)"})
  @UIFieldGroup("Port")
  @UIFieldShowOnCondition("return context.get('coordinatorHandler') == 'EmberHandler'")
  public int getFlowControl() {
    return getJsonData("fc", 1);
  }

  @UIField(order = 1, hideOnEmpty = true)
  @UIFieldTreeNodeSelection(rootPath = "${root}/zigbee")
  @UIFieldGroup(value = "Network", order = 10, borderColor = "#4f8a4e")
  public String getNetworkId() {
    return getJsonData("nid");
  }

  @UIField(order = 2, advanced = true, hideOnEmpty = true)
  @UIFieldGroup("Network")
  public String getExtendedPanId() {
    return getJsonData("epid", "0000000000000000");
  }

  @UIField(order = 3, advanced = true)
  @UIFieldGroup(value = "Network", order = 10, borderColor = "#4f8a4e")
  public String getNetworkKey() {
    return getJsonData("nk", "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
  }

  @UIField(order = 4, advanced = true)
  @UIFieldSlider(min = 0, max = 65535)
  @UIFieldGroup("Network")
  public int getPanId() {
    return getJsonData("pid", 65535);
  }

  @UIField(order = 5, advanced = true)
  @UIFieldGroup("Network")
  @UIFieldStaticSelection({"11..25;Channel %s"})
  public int getChannelId() {
    return getJsonData("cid", 11);
  }

  @UIField(order = 6, hideOnEmpty = true, advanced = true)
  @UIFieldGroup("Network")
  public String getLinkKey() {
    return getJsonData("lk", "");
  }

  @UIField(order = 1)
  @UIFieldSlider(min = 60, max = 254)
  @UIFieldGroup(value = "Discovery", order = 15, borderColor = "#663453")
  public int getDiscoveryDuration() {
    return getJsonData("dd", 254);
  }

  @UIField(order = 2)
  @UIFieldGroup("Discovery")
  public boolean isJoinDeviceDuringScanOnly() {
    return getJsonData("jddso", true);
  }

  @UIField(order = 3, advanced = true)
  @UIFieldStaticSelection({"0:Never", "300:5 Minutes", "1800:30 Minutes", "3600:1 Hour", "21600:6 Minutes",
      "86400:1 Day", "604800:1 Week"})
  @UIFieldGroup("Discovery")
  public int getMeshUpdatePeriod() {
    return getJsonData("mup", 86400);
  }

  @UIField(order = 1, advanced = true)
  @UIFieldStaticSelection({"-1:None", "0:Deny", "1:Insecure", "2:Secure", "3:InstallCode"})
  @UIFieldGroup(value = "Dongle", order = 20, borderColor = "#3E7792")
  public int getTrustCentreJoinMode() {
    return getJsonData("tc", -1);
  }

  @UIField(order = 2, advanced = true)
  @UIFieldSlider(min = 0, max = 8)
  @UIFieldGroup("Dongle")
  public int getTxPower() {
    return getJsonData("txp", 0);
  }

  @UIField(order = 3, hideOnEmpty = true, advanced = true)
  @UIFieldGroup("Dongle")
  public String getInstallCode() {
    return getJsonData("ic", "");
  }

  @UIField(order = 4, advanced = true)
  @UIFieldStaticSelection(value = {"0:Normal", "1:Boost"})
  @UIFieldGroup("Dongle")
  @UIFieldShowOnCondition("return context.get('coordinatorHandler') == 'EmberHandler'")
  public int getPowerMode() {
    return getJsonData("pm", 1);
  }

  @UIField(readOnly = true, order = 100, hideOnEmpty = true)
  @UIFieldGroup("Node")
  public String getLocalIeeeAddress() {
    return getJsonData("lia");
  }

  public void setLocalIeeeAddress(String value) {
    setJsonData("lia", value);
  }

  @UIContextMenuAction(value = "zigbee.start_scan", icon = "fas fa-search-location")
  public ActionResponseModel scan() {
    getService().getDiscoveryService().startScan();
    return ActionResponseModel.showSuccess("SUCCESS");
  }

  @Override
  public void logChangeStatus(Status status, String message) {
    Level level = status == Status.ERROR ? Level.ERROR : Level.INFO;
    if (StringUtils.isEmpty(message)) {
      log.log(level, "Set ZigBee coordinator status: {}", status);
    } else {
      log.log(level, "Set ZigBee coordinator status: {}. Msg: {}", status, message);
    }
  }

  @Override
  protected void beforePersist() {
    fixEntity();
  }

  @Override
  protected void beforeUpdate() {
    fixEntity();
  }

  // do not change Status on create service
  @Override
  public @Nullable Status getSuccessServiceStatus() {
    return null;
  }

  @JsonIgnore
  public Set<ZigBeeDeviceEntity> getOnlineDevices() {
    return getDevices().stream().filter(d -> d.getStatus() == Status.ONLINE).collect(Collectors.toSet());
  }

  @Override
  public Class<ZigBeeCoordinatorService> getEntityServiceItemClass() {
    return ZigBeeCoordinatorService.class;
  }

  @Override
  public ZigBeeCoordinatorService createService(EntityContext entityContext) {
    return getCoordinatorHandler().coordinatorSupplier.apply(entityContext);
  }

  public ZigbeeCoordinatorEntity nodeUpdated(ZigBeeNode node, EntityContext entityContext) {
    if (this.updateFromNodeDescriptor(node)) {
      return entityContext.save(this);
    }
    return this;
  }

  @RequiredArgsConstructor
  private enum ZigbeeCoordinator {
    CC2531Handler(entityContext -> new CC2531Service(entityContext));

    private final Function<EntityContext, ZigBeeCoordinatorService> coordinatorSupplier;
  }

  public void setPortBaud(int value) {
    setJsonData("pb", value);
  }

  public void setExtendedPanId(String value) {
    setJsonData("epid", value);
  }

  public void setFlowControl(int value) {
    setJsonData("fc", value);
  }

  public void setPort(String value) {
    setJsonData("port", value);
  }

  public void setLogEvents(boolean value) {
    setJsonData("le", value);
  }

  public void setNetworkId(String value) {
    setJsonData("nid", value);
  }

  public void setInstallCode(String value) {
    setJsonData("ic", value);
  }

  public void setJoinDeviceDuringScanOnly(boolean value) {
    setJsonData("jddso", value);
  }

  public void setLinkKey(String value) {
    setJsonData("lk", value);
  }

  public void setMeshUpdatePeriod(int value) {
    setJsonData("mup", value);
  }

  public void setPanId(int value) {
    setJsonData("pid", value);
  }

  public void setNetworkKey(String value) {
    setJsonData("nk", value);
  }

  public void setDiscoveryDuration(int value) {
    setJsonData("dd", value);
  }

  public void setCoordinatorHandler(ZigbeeCoordinator zigbeeCoordinator) {
    setJsonDataEnum("ch", zigbeeCoordinator);
  }

  public void setChannelId(int value) {
    setJsonData("cid", value);
  }

  public void setPowerMode(int value) {
    setJsonData("pm", value);
  }

  public void setTrustCentreJoinMode(int value) {
    setJsonData("tc", value);
  }

  public void setTxPower(int value) {
    setJsonData("txp", value);
  }

  public ZigbeeCoordinatorEntity setStart(boolean start) {
    setJsonData("start", start);
    return this;
  }

  private void fixEntity() {
    // fix network id
    setNetworkId(StringUtils.defaultIfEmpty(getNetworkId(), UUID.randomUUID().toString()));

    // fix network key
    ZigBeeKey zigBeeKey;
    try {
      zigBeeKey = new ZigBeeKey(getNetworkKey());
    } catch (IllegalArgumentException e) {
      zigBeeKey = new ZigBeeKey();
      log.debug("Network Key String has invalid format. Revert to default key [{}]", getNetworkKey());
    }
    if (!zigBeeKey.isValid()) {
      zigBeeKey = ZigBeeKey.createRandom();
      log.debug("Network key initialised {}", zigBeeKey);
    }
    setNetworkKey(zigBeeKey.toString());

    // fix pan id
    if (getPanId() == 0) {
      setPanId((int) Math.floor((Math.random() * 65534)));
      log.debug("Create random ZigBee PAN ID [{}]", String.format("%04X", getPanId()));
    }

    // fix extended pan id
    if (StringUtils.isNotEmpty(getExtendedPanId())) {
      ExtendedPanId extendedPanId = new ExtendedPanId(getExtendedPanId());
      if (!extendedPanId.isValid()) {
        int[] pan = new int[8];
        for (int cnt = 0; cnt < 8; cnt++) {
          pan[cnt] = (int) Math.floor((Math.random() * 255));
        }
        extendedPanId = new ExtendedPanId(pan);
        log.debug("Created random ZigBee extended PAN ID [{}]", extendedPanId);
        setExtendedPanId(extendedPanId.toString());
      }
    }

    // fix link key
    try {
      new ZigBeeKey(getLinkKey());
    } catch (IllegalArgumentException e) {
      setLinkKey(KEY_ZIGBEE_ALLIANCE_O9.toString());
      log.debug("Link Key String has invalid format. Revert to default key");
    }
  }
}
