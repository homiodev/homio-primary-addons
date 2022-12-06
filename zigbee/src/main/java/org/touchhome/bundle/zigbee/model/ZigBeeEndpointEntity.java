package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Optional;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextSetting;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.entity.PinBaseEntity;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldNumber;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.item.UISliderItemBuilder.SliderType;
import org.touchhome.bundle.api.ui.field.action.v1.layout.UIFlexLayoutBuilder;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.condition.UIFieldShowOnCondition;
import org.touchhome.bundle.api.ui.field.selection.UIFieldStaticSelection;
import org.touchhome.bundle.zigbee.ZigBeeEndpointUUID;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.config.ZclDoorLockConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclFanControlConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclLevelControlConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclOnOffSwitchConfig;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;

@Log4j2
@Entity
@Setter
@Getter
@Accessors(chain = true)
public class ZigBeeEndpointEntity extends PinBaseEntity<ZigBeeEndpointEntity, ZigBeeDeviceEntity>
    implements HasJsonData,
        HasStatusAndMsg<ZigBeeEndpointEntity>,
        EntityService<ZigbeeEndpointService, ZigBeeEndpointEntity>,
        HasDynamicContextMenuActions {

  // uses for changes inside cluster configuration to mark that entity has to be saved
  @Getter @Setter @JsonIgnore private transient boolean outdated;

  @Override
  public ZigBeeEndpointEntity setStatus(@Nullable Status status, @Nullable String msg) {
    EntityContextSetting.setStatus(this, "", "Status", status, msg);
    getEntityContext()
        .ui()
        .updateInnerSetItem(getOwner(), "endpointClusters", this, "status", status);
    return this;
  }

  @Override
  @UIField(order = 10, disableEdit = true)
  @UIFieldColorStatusMatch
  public Status getStatus() {
    return EntityContextSetting.getStatus(this, "", Status.UNKNOWN);
  }

  @Override
  @UIField(order = 11, hideInView = true, disableEdit = true, hideOnEmpty = true)
  public String getStatusMessage() {
    return EntityContextSetting.getMessage(this, "");
  }

  @UIField(order = 12, disableEdit = true)
  @UIFieldColorStatusMatch
  public Status getDeviceInitializeStatus(Status status) {
    return EntityContextSetting.getStatus(this, "DeviceInitializeStatus", Status.UNKNOWN);
  }

  public void setDeviceInitializeStatus(Status status) {
    EntityContextSetting.setStatus(
        this, "DeviceInitializeStatus", "DeviceInitializeStatus", status);
  }

  @UIField(order = 2, disableEdit = true)
  @UIFieldGroup(value = "General", order = 1, borderColor = "#317175")
  public int getClusterId() {
    return getJsonData().getInt("c_id");
  }

  public ZigBeeEndpointEntity setClusterId(int clusterId) {
    setJsonData("c_id", clusterId);
    return this;
  }

  @Override
  @UIField(order = 3, disableEdit = true, label = "endpointId")
  @UIFieldGroup("General")
  public int getAddress() {
    return super.getAddress();
  }

  @Override
  @UIField(order = 4)
  @UIFieldGroup("General")
  public String getName() {
    return super.getName();
  }

  @UIField(order = 5, disableEdit = true, type = UIFieldType.Duration)
  @UIFieldGroup("General")
  public long getLastAnswerFromEndpoint() {
    return EntityContextSetting.getMemValue(this, "lafe", 0L);
  }

  public void setLastAnswerFromEndpoint(long currentTimeMillis) {
    EntityContextSetting.setMemValue(this, "lafe", "LastAnswerFromEndpoint", currentTimeMillis);
    getEntityContext()
        .ui()
        .updateInnerSetItem(getOwner(), "endpointClusters", this, "updated", currentTimeMillis);
  }

  @UIField(order = 6, disableEdit = true)
  @UIFieldGroup("General")
  public String getValue() {
    return Optional.ofNullable(getLastState()).map(State::toString).orElse("");
  }

  public void setValue(State state) {
    EntityContextSetting.setMemValue(this, "last", "Value", state);
    getEntityContext()
        .ui()
        .updateInnerSetItem(getOwner(), "endpointClusters", this, "value", state.toString());
  }

  @JsonIgnore
  public State getLastState() {
    return EntityContextSetting.getMemValue(this, "last", null);
  }

  @UIField(order = 5, hideInView = true, disableEdit = true)
  @UIFieldGroup("General")
  public String getDescription() {
    return super.getDescription();
  }

  public String getIeeeAddress() {
    return getJsonData().getString("ieee");
  }

  public ZigBeeEndpointEntity setIeeeAddress(String value) {
    setJsonData("ieee", value);
    return this;
  }

  // The minimum time period in seconds between device state updates
  @UIField(order = 100)
  @UIFieldShowOnCondition("return context.get('supportReporting') == 'true'")
  @UIFieldNumber(min = 1, max = 86400)
  @UIFieldGroup(value = "Reporting", order = 2, borderColor = "#517531")
  public int getReportingTimeMin() {
    return getJsonData("rt_min", 1);
  }

  public ZigBeeEndpointEntity setReportingTimeMin(int value) {
    setJsonData("rt_min", value);
    return this;
  }

  // The maximum time period in seconds between device state updates
  @UIField(order = 101)
  @UIFieldShowOnCondition("return context.get('supportReporting') == 'true'")
  @UIFieldNumber(min = 1, max = 86400)
  @UIFieldGroup("Reporting")
  public int getReportingTimeMax() {
    return getJsonData("rt_max", 900);
  }

  public ZigBeeEndpointEntity setReportingTimeMax(int value) {
    setJsonData("rt_max", value);
    return this;
  }

  @UIField(order = 102)
  @UIFieldShowOnCondition(
      "return context.get('supportAnalogue') == 'true'") // is analogue is true, then
  // 'supportReporting' also true
  @UIFieldNumber(minRef = "reportingChangeMin", maxRef = "reportingChangeMax")
  @UIFieldGroup("Reporting")
  public double getReportingChange() {
    return getJsonData("rt_ch", 1);
  }

  @UIField(order = 103)
  @UIFieldShowOnCondition("return context.get('supportReporting') == 'true'")
  @UIFieldNumber(min = 15, max = 86400)
  @UIFieldGroup("Reporting")
  public int getPollingPeriod() {
    return getJsonData("pp", 7200);
  }

  public ZigBeeEndpointEntity setPollingPeriod(int value) {
    if (value != getPollingPeriod()) {
      setJsonData("pp", value);
    }
    return this;
  }

  // options.add(new ParameterOption("65535", "Use On/Off times"));
  @UIField(order = 200)
  @UIFieldShowOnCondition("return context.get('supportLevelControl') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup(value = "LevelControl", order = 3, borderColor = "#35786f")
  public int getDefaultTransitionTime() {
    return getJsonData("ttc", 0);
  }

  public ZigBeeEndpointEntity setDefaultTransitionTime(int value) {
    setJsonData("ttc", value);
    return this;
  }

  @UIField(order = 201)
  @UIFieldShowOnCondition("return context.get('supportLevelControl') == 'true'")
  @UIFieldGroup("LevelControl")
  public boolean getInvertLevelControl() {
    return getJsonData("ilc", false);
  }

  public ZigBeeEndpointEntity setInvertLevelControl(boolean value) {
    setJsonData("ilc", value);
    return this;
  }

  @UIField(order = 202)
  @UIFieldShowOnCondition("return context.get('supportLevelControl') == 'true'")
  @UIFieldGroup("LevelControl")
  public boolean getInvertReportControl() {
    return getJsonData("irc", false);
  }

  public ZigBeeEndpointEntity setInvertReportControl(boolean value) {
    setJsonData("irc", value);
    return this;
  }

  @UIField(order = 203)
  @UIFieldShowOnCondition("return context.get('supportOnOffTransitionTime') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup("LevelControl")
  public int getOnOffTransitionTime() {
    return getJsonData("onOffTT", 0);
  }

  public ZigBeeEndpointEntity setOnOffTransitionTime(int value) {
    setJsonData("onOffTT", value);
    return this;
  }

  // options.add(new ParameterOption("65535", "Use On/Off transition time"));
  @UIField(order = 204)
  @UIFieldShowOnCondition("return context.get('supportOnTransitionTime') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup("LevelControl")
  public int getOnTransitionTime() {
    return getJsonData("onTT", 65535);
  }

  public ZigBeeEndpointEntity setOnTransitionTime(int value) {
    setJsonData("onTT", value);
    return this;
  }

  // options.add(new ParameterOption("65535", "Use On/Off transition time"));
  @UIField(order = 205)
  @UIFieldShowOnCondition("return context.get('supportOffTransitionTime') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup("LevelControl")
  public int getOffTransitionTime() {
    return getJsonData("offTT", 65535);
  }

  public ZigBeeEndpointEntity setOffTransitionTime(int value) {
    setJsonData("offTT", value);
    return this;
  }

  // options.add(new ParameterOption("255", "Not Set"));
  @UIField(order = 206)
  @UIFieldShowOnCondition("return context.get('supportOnLevel') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup("LevelControl")
  public int getOnLevel() {
    return getJsonData("onLvl", 255);
  }

  public ZigBeeEndpointEntity setOnLevel(int value) {
    setJsonData("onLvl", value);
    return this;
  }

  // options.add(new ParameterOption("255", "Not Set"));
  @UIField(order = 207)
  @UIFieldShowOnCondition("return context.get('supportDefaultMoveRate') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup("LevelControl")
  public int getDefaultMoveRate() {
    return getJsonData("defMoveRate", 255);
  }

  public ZigBeeEndpointEntity setDefaultMoveRate(int value) {
    setJsonData("defMoveRate", value);
    return this;
  }

  @UIField(order = 300)
  @UIFieldShowOnCondition("return context.get('supportOffWaitTime') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup(value = "OnOffSwitch", order = 20, borderColor = "#B58A35")
  public int getOffWaitTime() {
    return getJsonData("offWaitTime", 0);
  }

  public ZigBeeEndpointEntity setOffWaitTime(int value) {
    setJsonData("offWaitTime", value);
    return this;
  }

  @UIField(order = 301)
  @UIFieldShowOnCondition("return context.get('supportOnTime') == 'true'")
  @UIFieldNumber(min = 0, max = 60000)
  @UIFieldGroup("OnOffSwitch")
  public int getOnTime() {
    return getJsonData("onTime", 65535);
  }

  public ZigBeeEndpointEntity setOnTime(int value) {
    setJsonData("onTime", value);
    return this;
  }

  @UIField(order = 302)
  @UIFieldShowOnCondition("return context.get('supportStartupOnOff') == 'true'")
  @UIFieldGroup("OnOffSwitch")
  public boolean getStartupOnOff() {
    return getJsonData("startupOnOff", false);
  }

  public ZigBeeEndpointEntity setStartupOnOff(boolean value) {
    setJsonData("startupOnOff", value);
    return this;
  }

  @UIField(order = 400)
  @UIFieldShowOnCondition("return context.get('supportFanModeSequence') == 'true'")
  @UIFieldStaticSelection({
    "0:Low/Med/High",
    "1:Low/High",
    "2:Low/Med/High/Auto",
    "3:Low/High/Auto",
    "4:On/Auto"
  })
  @UIFieldGroup("Fan")
  public int getFanModeSequence() {
    return getJsonData("fanModeSeq", 4);
  }

  public void setFanModeSequence(int value) {
    setJsonData("fanModeSeq", value);
  }

  /**
   * TODO: case 0: options.add(new StateOption("1", "Low")); options.add(new StateOption("2",
   * "Medium")); options.add(new StateOption("3", "High")); case 1: options.add(new StateOption("1",
   * "Low")); options.add(new StateOption("3", "High")); break; case 2: options.add(new
   * StateOption("1", "Low")); options.add(new StateOption("2", "Medium")); options.add(new
   * StateOption("3", "High")); options.add(new StateOption("5", "Auto")); break; case 3:
   * options.add(new StateOption("1", "Low")); options.add(new StateOption("3", "High"));
   * options.add(new StateOption("5", "Auto")); break; case 4: options.add(new StateOption("4",
   * "On")); options.add(new StateOption("5", "Auto")); break;
   */
  @UIField(order = 500)
  @UIFieldStaticSelection({"0:Silent", "1:Low", "2:High"})
  @UIFieldShowOnCondition("return context.get('supportSoundVolume') == 'true'")
  @UIFieldGroup("DoorLock")
  public int getSoundVolume() {
    return getJsonData("dl_sv", 4);
  }

  public void setSoundVolume(int value) {
    setJsonData("dl_sv", value);
  }

  @UIField(order = 501)
  @UIFieldSlider(min = 0, max = 3600)
  @UIFieldShowOnCondition("return context.get('supportAutoRelockTime') == 'true'")
  @UIFieldGroup("DoorLock")
  public int getEnableAutoRelockTime() {
    return getJsonData("dl_art", 0);
  }

  public void setEnableAutoRelockTime(int value) {
    setJsonData("dl_art", value);
  }

  @UIField(order = 502)
  @UIFieldShowOnCondition("return context.get('supportLocalProgramming') == 'true'")
  @UIFieldGroup("DoorLock")
  public boolean getEnableLocalProgramming() {
    return getJsonData("dl_lp", false);
  }

  public void setEnableLocalProgramming(boolean value) {
    setJsonData("dl_lp", value);
  }

  @UIField(order = 503)
  @UIFieldShowOnCondition("return context.get('supportEnableOneTouchLocking') == 'true'")
  @UIFieldGroup("DoorLock")
  public boolean getEnableOneTouchLocking() {
    return getJsonData("dl_otl", false);
  }

  public void setEnableOneTouchLocking(boolean value) {
    setJsonData("dl_otl", value);
  }

  @UIField(order = 600)
  @UIFieldShowOnCondition("return context.get('supportColorControl') == 'true'")
  @UIFieldGroup("ColorControl")
  public ControlMethod getColorControlMethod() {
    return getJsonDataEnum("cc_ccm", ControlMethod.AUTO);
  }

  public void setColorControlMethod(ControlMethod value) {
    setJsonDataEnum("cc_ccm", value);
  }

  // configurable by cluster if analogue is true
  public int getReportingChangeMin() {
    return getJsonData("rt_ch_min", 0);
  }

  // configurable by cluster if analogue is true
  public int getReportingChangeMax() {
    return getJsonData("rt_ch_max", 0);
  }

  public boolean isSupportColorControl() {
    return optService().map(s -> s.getCluster().isSupportConfigColorControl()).orElse(false);
  }

  public boolean isSupportOffWaitTime() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigOnOff())
                    .map(ZclOnOffSwitchConfig::isSupportOffWaitTime)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportOnTime() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigOnOff())
                    .map(ZclOnOffSwitchConfig::isSupportOnTime)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportStartupOnOff() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigOnOff())
                    .map(ZclOnOffSwitchConfig::isSupportStartupOnOff)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportFanModeSequence() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigFanControl())
                    .map(ZclFanControlConfig::isSupportFanModeSequence)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportSoundVolume() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigDoorLock())
                    .map(ZclDoorLockConfig::isSupportSoundVolume)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportAutoRelockTime() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigDoorLock())
                    .map(ZclDoorLockConfig::isSupportAutoRelockTime)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportEnableOneTouchLocking() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigDoorLock())
                    .map(ZclDoorLockConfig::isSupportEnableOneTouchLocking)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportLocalProgramming() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigDoorLock())
                    .map(ZclDoorLockConfig::isSupportLocalProgramming)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportAnalogue() {
    return getJsonData("analogue", false);
  }

  public boolean isSupportLevelControl() {
    return optService().map(s -> s.getCluster().getConfigLevelControl() != null).orElse(false);
  }

  public boolean isSupportReporting() {
    return optService().map(s -> s.getCluster().getConfigReporting() != null).orElse(false);
  }

  public boolean isSupportOnOffTransitionTime() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigLevelControl())
                    .map(ZclLevelControlConfig::isSupportOnOffTransitionTime)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportOnTransitionTime() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigLevelControl())
                    .map(ZclLevelControlConfig::isSupportOnTransitionTime)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportOffTransitionTime() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigLevelControl())
                    .map(ZclLevelControlConfig::isSupportOffTransitionTime)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportOnLevel() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigLevelControl())
                    .map(ZclLevelControlConfig::isSupportOnLevel)
                    .orElse(false))
        .orElse(false);
  }

  public boolean isSupportDefaultMoveRate() {
    return optService()
        .map(
            s ->
                Optional.ofNullable(s.getCluster().getConfigLevelControl())
                    .map(ZclLevelControlConfig::isSupportDefaultMoveRate)
                    .orElse(false))
        .orElse(false);
  }

  @Override
  public Class<ZigbeeEndpointService> getEntityServiceItemClass() {
    return ZigbeeEndpointService.class;
  }

  @Override
  public ZigbeeEndpointService createService(@NotNull EntityContext entityContext) {
    return null;
  }

  @Override
  public String toString() {
    return "ZigBee endpoint '"
        + getTitle()
        + "'. [ieeeAddress='"
        + getIeeeAddress()
        + "', clusterId="
        + getClusterId()
        + ", endpointId="
        + getAddress()
        + ", clusterName='"
        + getName()
        + "']";
  }

  @JsonIgnore
  public ZigBeeEndpointUUID getEndpointUUID() {
    return new ZigBeeEndpointUUID(getIeeeAddress(), getClusterId(), getAddress(), getName());
  }

  public void setAnalogue(Double defaultChange, Integer minimumChange, Integer maximumChange) {
    setJsonData("analogue", true);
    if (minimumChange != getReportingChangeMin()) {
      setJsonData("rt_ch_min", minimumChange);
    }
    if (maximumChange != getReportingChangeMax()) {
      setJsonData("rt_ch_max", maximumChange);
    }
    if (defaultChange != getReportingChange()) {
      setJsonData("rt_ch", defaultChange);
    }
  }

  @Override
  public void assembleActions(UIInputBuilder builder) {
    optService()
        .ifPresent(
            service -> {
              ZigBeeBaseChannelConverter cluster = service.getCluster();
              builder.addSelectableButton(
                  "zigbee.action.pull_values",
                  "fas fa-download",
                  "#A939B7",
                  (entityContext, params) -> {
                    cluster.fireHandleRefresh();
                    return ActionResponseModel.success();
                  });
              assembleReportConfigActions(builder);
              assembleLevelControlActions(builder);
              assembleOnOfSwitchActions(builder);

              // TODO: not all actions are assembled!!! i.e. fan, door lock, etc...

              cluster.assembleActions(builder);
            });
  }

  private void assembleOnOfSwitchActions(UIInputBuilder builder) {
    if (isSupportOffWaitTime() || isSupportOnTime() || isSupportStartupOnOff()) {
      UIFlexLayoutBuilder onOffSwitchFlex =
          builder.addFlex("OnOffSwitch").setBorderArea("OnOffSwitch").setBorderColor("#B58A35");

      if (isSupportOffWaitTime()) {
        onOffSwitchFlex.addSlider(
            "field.offWaitTime",
            getOffWaitTime(),
            0,
            60000,
            (entityContext, params) -> {
              if (getOffWaitTime() != params.getInt("value")) {
                entityContext.save(setOffWaitTime(params.getInt("value")));
                return ActionResponseModel.success();
              }
              return null;
            },
            SliderType.Regular);
      }

      if (isSupportOnTime()) {
        UIFlexLayoutBuilder onTimeFlex =
            onOffSwitchFlex.addFlex("on-time", 3).columnFlexDirection();
        onTimeFlex.addButton(
            "Not Set", null, null, (entityContext, params) -> updateOnTime(entityContext, 65535));
        onTimeFlex.addSlider(
            "field.onTime",
            getOnTime(),
            0,
            60000,
            (entityContext, params) -> updateOnTime(entityContext, params.getInt("value")),
            SliderType.Regular);
      }

      if (isSupportStartupOnOff()) {
        onOffSwitchFlex.addCheckbox(
            "field.startupOnOff",
            getStartupOnOff(),
            (entityContext, params) -> {
              if (getStartupOnOff() != params.getBoolean("value")) {
                entityContext.save(setStartupOnOff(params.getBoolean("value")));
                return ActionResponseModel.success();
              }
              return null;
            });
      }
    }
  }

  private void assembleLevelControlActions(UIInputBuilder builder) {
    if (isSupportLevelControl()) {
      UIFlexLayoutBuilder levelControlFlex =
          builder.addFlex("LevelControl").setBorderArea("LevelControl").setBorderColor("#35786f");

      UIFlexLayoutBuilder defaultTransitionTimeFlex =
          levelControlFlex.addFlex("def-trans-time", 0).columnFlexDirection();
      defaultTransitionTimeFlex.addButton(
          "Use On/Off times",
          null,
          null,
          (entityContext, params) -> updateDefaultTransitionTime(entityContext, 65535));
      defaultTransitionTimeFlex.addSlider(
          "field.defaultTransitionTime",
          getDefaultTransitionTime(),
          0,
          60000,
          (entityContext, params) ->
              updateDefaultTransitionTime(entityContext, params.getInt("value")),
          SliderType.Regular);

      levelControlFlex.addCheckbox(
          "field.invertLevelControl",
          getInvertLevelControl(),
          (entityContext, params) -> {
            if (getInvertLevelControl() != params.getBoolean("value")) {
              entityContext.save(setInvertLevelControl(params.getBoolean("value")));
              return ActionResponseModel.success();
            }
            return null;
          });

      levelControlFlex.addCheckbox(
          "field.invertLevelControl",
          getInvertLevelControl(),
          (entityContext, params) -> {
            if (getInvertLevelControl() != params.getBoolean("value")) {
              entityContext.save(setInvertLevelControl(params.getBoolean("value")));
              return ActionResponseModel.success();
            }
            return null;
          });

      levelControlFlex.addCheckbox(
          "field.invertReportControl",
          getInvertReportControl(),
          (entityContext, params) -> {
            if (getInvertReportControl() != params.getBoolean("value")) {
              entityContext.save(setInvertReportControl(params.getBoolean("value")));
              return ActionResponseModel.success();
            }
            return null;
          });

      if (isSupportOnOffTransitionTime()) {
        levelControlFlex.addSlider(
            "field.onOffTransitionTime",
            getOnOffTransitionTime(),
            0,
            60000,
            (entityContext, params) -> {
              if (getOnOffTransitionTime() != params.getInt("value")) {
                entityContext.save(setOnOffTransitionTime(params.getInt("value")));
                return ActionResponseModel.success();
              }
              return null;
            },
            SliderType.Regular);
      }

      if (isSupportOnTransitionTime()) {
        UIFlexLayoutBuilder onTransitionTimeFlex =
            levelControlFlex.addFlex("on-trans-time", 1).columnFlexDirection();
        onTransitionTimeFlex.addButton(
            "Use On/Off transition time",
            null,
            null,
            (entityContext, params) -> updateOnTransitionTime(entityContext, 65535));
        onTransitionTimeFlex.addSlider(
            "field.defaultTransitionTime",
            getOnTransitionTime(),
            0,
            60000,
            (entityContext, params) ->
                updateOnTransitionTime(entityContext, params.getInt("value")),
            SliderType.Regular);
      }

      if (isSupportOffTransitionTime()) {
        UIFlexLayoutBuilder offTransitionTimeFlex =
            levelControlFlex.addFlex("off-trans-time", 2).columnFlexDirection();
        offTransitionTimeFlex.addButton(
            "Use On/Off transition time",
            null,
            null,
            (entityContext, params) -> updateOffTransitionTime(entityContext, 65535));
        offTransitionTimeFlex.addSlider(
            "field.offTransitionTime",
            getOffTransitionTime(),
            0,
            60000,
            (entityContext, params) ->
                updateOffTransitionTime(entityContext, params.getInt("value")),
            SliderType.Regular);
      }

      if (isSupportOnLevel()) {
        UIFlexLayoutBuilder onLevelFlex =
            levelControlFlex.addFlex("on-level", 3).columnFlexDirection();
        onLevelFlex.addButton(
            "Not Set", null, null, (entityContext, params) -> updateOnLevel(entityContext, 255));
        onLevelFlex.addSlider(
            "field.offTransitionTime",
            getOnLevel(),
            0,
            60000,
            (entityContext, params) -> updateOnLevel(entityContext, params.getInt("value")),
            SliderType.Regular);
      }

      if (isSupportDefaultMoveRate()) {
        UIFlexLayoutBuilder defaultMoveRateFlex =
            levelControlFlex.addFlex("def-move-rate", 4).columnFlexDirection();
        defaultMoveRateFlex.addButton(
            "Not Set",
            null,
            null,
            (entityContext, params) -> updateDefaultMoveRate(entityContext, 255));
        defaultMoveRateFlex.addSlider(
            "field.defaultMoveRate",
            getDefaultMoveRate(),
            0,
            60000,
            (entityContext, params) -> updateDefaultMoveRate(entityContext, params.getInt("value")),
            SliderType.Regular);
      }
    }
  }

  private void assembleReportConfigActions(UIInputBuilder builder) {
    if (isSupportReporting()) {
      UIFlexLayoutBuilder reportFlex =
          builder.addFlex("Reporting").setBorderArea("Reporting").setBorderColor("#517531");

      reportFlex.addSlider(
          "field.reportingTimeMin",
          getReportingTimeMin(),
          1,
          86400,
          (entityContext, params) -> {
            if (getReportingTimeMin() != params.getInt("value")) {
              entityContext.save(setReportingTimeMin(params.getInt("value")));
              return ActionResponseModel.success();
            }
            return null;
          },
          SliderType.Regular);

      reportFlex.addSlider(
          "field.reportingTimeMax",
          getReportingTimeMax(),
          1,
          86400,
          (entityContext, params) -> {
            if (getReportingTimeMax() != params.getInt("value")) {
              entityContext.save(setReportingTimeMax(params.getInt("value")));
              return ActionResponseModel.success();
            }
            return null;
          },
          SliderType.Regular);

      reportFlex.addSlider(
          "field.pollingPeriod",
          getPollingPeriod(),
          15,
          86400,
          (entityContext, params) -> {
            if (getPollingPeriod() != params.getInt("value")) {
              entityContext.save(setPollingPeriod(params.getInt("value")));
              return ActionResponseModel.success();
            }
            return null;
          },
          SliderType.Regular);

      if (isSupportAnalogue()) {
        reportFlex.addSlider(
            "field.reportingChange",
            getPollingPeriod(),
            getReportingChangeMin(),
            getReportingChangeMax(),
            (entityContext, params) -> {
              if (getReportingChange() != params.getInt("value")) {
                entityContext.save(setPollingPeriod(params.getInt("value")));
                return ActionResponseModel.success();
              }
              return null;
            },
            SliderType.Regular);
      }
    }
  }

  private ActionResponseModel updateDefaultTransitionTime(EntityContext entityContext, int value) {
    if (getDefaultTransitionTime() != value) {
      entityContext.save(setDefaultTransitionTime(value));
      return ActionResponseModel.success();
    }
    return null;
  }

  private ActionResponseModel updateOnTransitionTime(EntityContext entityContext, int value) {
    if (getOnTransitionTime() != value) {
      entityContext.save(setOnTransitionTime(value));
      return ActionResponseModel.success();
    }
    return null;
  }

  private ActionResponseModel updateOffTransitionTime(EntityContext entityContext, int value) {
    if (getOffTransitionTime() != value) {
      entityContext.save(setOffTransitionTime(value));
      return ActionResponseModel.success();
    }
    return null;
  }

  private ActionResponseModel updateOnLevel(EntityContext entityContext, int value) {
    if (getOnLevel() != value) {
      entityContext.save(setOnLevel(value));
      return ActionResponseModel.success();
    }
    return null;
  }

  private ActionResponseModel updateDefaultMoveRate(EntityContext entityContext, int value) {
    if (getDefaultMoveRate() != value) {
      entityContext.save(setDefaultMoveRate(value));
      return ActionResponseModel.success();
    }
    return null;
  }

  private ActionResponseModel updateOnTime(EntityContext entityContext, int value) {
    if (getOnTime() != value) {
      entityContext.save(setOnTime(value));
      return ActionResponseModel.success();
    }
    return null;
  }

  public enum ControlMethod {
    AUTO,
    HUE,
    XY
  }
}
