package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.entity.types.MicroControllerBaseEntity;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.UISidebarChildren;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.selection.UIFieldDevicePortSelection;
import org.touchhome.bundle.api.ui.field.selection.UIFieldStaticSelection;
import org.touchhome.bundle.api.ui.field.selection.UIFieldTreeNodeSelection;
import org.touchhome.bundle.zigbee.ZigBeeCoordinatorHandler;
import org.touchhome.bundle.zigbee.handler.CC2531Handler;

@Log4j2
@Entity
@UISidebarChildren(icon = "fas fa-circle-nodes", color = "#D46A06")
public final class ZigbeeCoordinatorEntity extends MicroControllerBaseEntity<ZigbeeCoordinatorEntity> {

  public static final String PREFIX = "zbc_";

  private static Map<String, ZigBeeCoordinatorHandler> zigBeeCoordinatorHandlerMap = new HashMap<>();

  @Getter
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "coordinatorEntity", cascade = CascadeType.REMOVE)
  private Set<ZigBeeDeviceEntity> devices;

  @Override
  public String getDefaultName() {
    return "ZigBee";
  }

  @Override
  public void afterFetch(EntityContext entityContext) {
    super.afterFetch(entityContext);
  }

  @Override
  protected void beforeDelete() {
    super.beforeDelete();
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  @UIField(order = 15, inlineEdit = true)
  public boolean isStart() {
    return getJsonData("start", false);
  }

  public ZigbeeCoordinatorEntity setStart(boolean start) {
    setJsonData("start", start);
    return this;
  }

  @UIField(order = 50)
  @UIFieldDevicePortSelection
  @RestartHandlerOnChange
  public String getPort() {
    return getJsonData("port", "");
  }

  public void setPort(String value) {
    setJsonData("port", value);
  }

  @UIField(order = 55, inlineEdit = true)
  public boolean isLogEvents() {
    return getJsonData("le", false);
  }

  public void setLogEvents(boolean value) {
    setJsonData("le", value);
  }

  @UIField(order = 60)
  @RestartHandlerOnChange
  @UIFieldTreeNodeSelection(rootPath = "${root}/zigbee")
  public String getNetworkId() {
    return getJsonData("nid", "");
  }

  public void setNetworkId(String value) {
    setJsonData("nid", value);
  }

  @UIField(order = 65)
  @UIFieldSlider(min = 60, max = 254)
  public int getDiscoveryDuration() {
    return getJsonData("dd", 254);
  }

  public ZigbeeCoordinatorEntity setDiscoveryDuration(int value) {
    setJsonData("dd", value);
    return this;
  }

  @UIField(order = 70)
  @RestartHandlerOnChange
  public ZigbeeCoordinator getHandler() {
    return getJsonDataEnum("ch", ZigbeeCoordinator.CC2531Handler);
  }

  public ZigbeeCoordinatorEntity setHandler(ZigbeeCoordinator zigbeeCoordinator) {
    setJsonDataEnum("ch", zigbeeCoordinator);
    return this;
  }

  /**
   * Advanced options
   */

  @UIField(order = 100, advanced = true)
  @RestartHandlerOnChange
  @UIFieldSlider(min = 11, max = 25)
  public int getChannelId() {
    return getJsonData("cid", 0);
  }

  public ZigbeeCoordinatorEntity setChannelId(int value) {
    setJsonData("cid", value);
    return this;
  }

  @UIField(order = 110, advanced = true)
  @RestartHandlerOnChange
  public String getExtendedPanId() {
    return getJsonData("epid", "");
  }

  public void setExtendedPanId(String value) {
    setJsonData("epid", value);
  }

  @UIField(order = 120, advanced = true)
  @RestartHandlerOnChange
  public String getInstallCode() {
    return getJsonData("ic", "");
  }

  public void setInstallCode(String value) {
    setJsonData("ic", value);
  }

  @UIField(order = 130)
  public boolean isJoinDeviceDuringScanOnly() {
    return getJsonData("jddso", true);
  }

  public void setJoinDeviceDuringScanOnly(boolean value) {
    setJsonData("jddso", value);
  }

  @UIField(order = 140, advanced = true)
  @RestartHandlerOnChange
  public String getLinkKey() {
    return getJsonData("lk", "");
  }

  public void setLinkKey(String value) {
    setJsonData("lk", value);
  }

  @UIField(order = 150, advanced = true)
  @RestartHandlerOnChange
  @UIFieldStaticSelection({"0:Never", "300:5 Minutes", "1800:30 Minutes", "3600:1 Hour", "21600:6 Minutes",
      "86400:1 Day", "604800:1 Week"})
  public int getMeshUpdatePeriod() {
    return getJsonData("mup", 86400);
  }

  public void setMeshUpdatePeriod(int value) {
    setJsonData("mup", value);
  }

  @UIField(order = 160, advanced = true)
  @RestartHandlerOnChange
  public String getNetworkKey() {
    return getJsonData("nk", "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
  }

  public void setNetworkKey(String value) {
    setJsonData("nk", value);
  }

  @UIField(order = 170, advanced = true)
  @UIFieldSlider(min = 0, max = 65535)
  @RestartHandlerOnChange
  public int getPanId() {
    return getJsonData("pid", 65535);
  }

  public ZigbeeCoordinatorEntity setPanId(int value) {
    setJsonData("pid", value);
    return this;
  }

  @UIField(order = 180, advanced = true)
  @RestartHandlerOnChange
  @UIFieldStaticSelection(value = {"38400", "57600", "115200"})
  public int getPortBaud() {
    return getJsonData("pb", 115200);
  }

  public void setPortBaud(int value) {
    setJsonData("pb", value);
  }

  @UIField(order = 190, advanced = true)
  @RestartHandlerOnChange
  @UIFieldStaticSelection(value = {"0:Normal", "1:Boost"})
  public int getPowerMode() {
    return getJsonData("pm", 1);
  }

  public void setPowerMode(int value) {
    setJsonData("pm", value);
  }

  @UIField(order = 200, advanced = true)
  @RestartHandlerOnChange
  @UIFieldStaticSelection({"-1:None", "0:Deny", "1:Insecure", "2:Secure", "3:InstallCode"})
  public int getTrustCentreJoinMode() {
    return getJsonData("tc", -1);
  }

  public void setTrustCentreJoinMode(int value) {
    setJsonData("tc", value);
  }

  @UIField(order = 210, advanced = true)
  @UIFieldSlider(min = 0, max = 8)
  @RestartHandlerOnChange
  public int getTxPower() {
    return getJsonData("txp", 0);
  }

  public ZigbeeCoordinatorEntity setTxPower(int value) {
    setJsonData("txp", value);
    return this;
  }

  @JsonIgnore // Used in Ember only!!!
  @UIField(order = 220, advanced = true)
  @RestartHandlerOnChange
  @UIFieldStaticSelection({"0:None", "1:Hardware (CTS/RTS)", "2:Software (XOn/XOff)"})
  public int getFlowControl() {
    return getJsonData("fc", 1);
  }

  public void setFlowControl(int value) {
    setJsonData("fc", value);
  }

  @UIContextMenuAction(value = "START_SCAN", icon = "fas fa-search-location")
  public ActionResponseModel scan() {
    getZigBeeCoordinatorHandler().getDiscoveryService().startScan();
    return ActionResponseModel.showSuccess("SUCCESS");
  }

  public void initialize() {
    log.info("Starting Zigbee: <{}>", getTitle());
    ZigBeeCoordinatorHandler coordinatorHandler = getZigBeeCoordinatorHandler();
    coordinatorHandler.dispose("");

    if (StringUtils.isEmpty(getPort())) {
      setStatusError("No zigbee coordinator port selected");
      return;
    }

    coordinatorHandler.setCoordinator(this);
    log.info("Done init Zigbee: <{}>", getTitle());
  }

  @Override
  public ZigbeeCoordinatorEntity setStatus(@NotNull Status status, @Nullable String msg) {
    log.log(status == Status.ERROR ? Level.ERROR : Level.INFO, "Zigbee status: {}. Msg: {}", status, msg);
    return super.setStatus(status, msg);
  }

  @Override
  public void afterUpdate(EntityContext entityContext) {
    ZigBeeCoordinatorHandler coordinatorHandler = getZigBeeCoordinatorHandler();

    if (isStart()) {
      if (!coordinatorHandler.isInitialized()) {
        initialize();
      } else {
        coordinatorHandler.restartIfRequire(this);
      }
    } else if (coordinatorHandler.isInitialized()) {
      getZigBeeCoordinatorHandler().dispose("stopped");
    }
  }

  public @NotNull ZigBeeCoordinatorHandler getZigBeeCoordinatorHandler() {
    return zigBeeCoordinatorHandlerMap.computeIfAbsent(getEntityID(), s ->
        getHandler().coordinatorSupplier.apply(getEntityContext()));
  }

  @JsonIgnore
  public Set<ZigBeeDeviceEntity> getOnlineDevices() {
    return getDevices().stream().filter(d -> d.getStatus() == Status.ONLINE).collect(Collectors.toSet());
  }

  @RequiredArgsConstructor
  private enum ZigbeeCoordinator {
    CC2531Handler(entityContext -> new CC2531Handler(entityContext));

    private final Function<EntityContext, ZigBeeCoordinatorHandler> coordinatorSupplier;
  }
}
