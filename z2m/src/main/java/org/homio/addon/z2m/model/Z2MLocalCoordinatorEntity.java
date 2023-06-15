package org.homio.addon.z2m.model;

import static org.homio.addon.z2m.util.ZigBeeUtil.zigbee2mqttGitHub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.Z2MLocalCoordinatorService;
import org.homio.addon.z2m.util.Z2MDeviceModel;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextService;
import org.homio.api.EntityContextService.MQTTEntityService;
import org.homio.api.entity.BaseEntity;
import org.homio.api.entity.types.MicroControllerBaseEntity;
import org.homio.api.entity.types.StorageEntity;
import org.homio.api.entity.validation.UIFieldValidationSize;
import org.homio.api.entity.zigbee.ZigBeeBaseCoordinatorEntity;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.entity.zigbee.ZigBeeProperty;
import org.homio.api.exception.ProhibitedExecution;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.HasFirmwareVersion;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.UISidebarChildren;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldGroup;
import org.homio.api.ui.field.UIFieldIgnore;
import org.homio.api.ui.field.UIFieldLinkToEntity;
import org.homio.api.ui.field.UIFieldSlider;
import org.homio.api.ui.field.action.UIContextMenuAction;
import org.homio.api.ui.field.inline.UIFieldInlineEntities;
import org.homio.api.ui.field.inline.UIFieldInlineEntityWidth;
import org.homio.api.ui.field.selection.UIFieldEntityTypeSelection;
import org.homio.api.util.DataSourceUtil;
import org.homio.api.util.DataSourceUtil.DataSourceContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "rawtypes"})
@Log4j2
@Entity
@UISidebarChildren(icon = "fas fa-circle-nodes", color = "#11A4C2")
public class Z2MLocalCoordinatorEntity extends MicroControllerBaseEntity<Z2MLocalCoordinatorEntity>
    implements HasFirmwareVersion, ZigBeeBaseCoordinatorEntity<Z2MLocalCoordinatorEntity, Z2MLocalCoordinatorService> {

    public static final String PREFIX = "zb2mqtt_";

    @UIField(order = 9999, disableEdit = true, hideInEdit = true)
    @UIFieldInlineEntities(bg = "#27FF000D")
    public List<ZigBeeCoordinatorDeviceEntity> getCoordinatorDevices() {
        return optService().map(service -> service.getDeviceHandlers().values().stream().map(ZigBeeCoordinatorDeviceEntity::new).collect(Collectors.toList()))
                           .orElse(Collections.emptyList());
    }

    @UIField(order = 20, required = true, inlineEditWhenEmpty = true)
    @UIFieldEntityTypeSelection(type = EntityContextService.MQTT_SERVICE)
    @UIFieldLinkToEntity(StorageEntity.class)
    public String getMqttEntity() {
        DataSourceContext source = DataSourceUtil.getSource(getEntityContext(), getJsonData("mqtt"));
        if (source.getSource() instanceof BaseEntity entity) {
            return entity.getEntityID() + "~~~" + entity.getTitle();
        }
        return getJsonData().has("mqtt") ? "[Not found] " + getJsonData("mqtt") : null;
    }

    @JsonIgnore
    public @Nullable MQTTEntityService getMqttEntityService() {
        return DataSourceUtil.getSource(getEntityContext(), getJsonData("mqtt")).getSource(MQTTEntityService.class, null);
    }

    public void setMqttEntity(String value) {
        setJsonData("mqtt", value);
    }

    @JsonIgnore
    public String getRawMqttEntity() {
        return getJsonData("mqtt");
    }

    @Override
    @UIFieldIgnore
    @JsonIgnore
    public String getPlace() {
        throw new ProhibitedExecution();
    }

    @Override
    public String getDefaultName() {
        return "ZigBee2MQTT";
    }

    @Override
    public @NotNull String getEntityPrefix() {
        return PREFIX;
    }

    @UIField(order = 30, inlineEdit = true)
    @UIFieldGroup("GENERAL")
    public boolean isPermitJoin() {
        return getJsonData("pj", false);
    }

    public void setPermitJoin(boolean value) {
        setJsonData("pj", value);
    }

    @UIField(order = 35, isRevert = true)
    @UIFieldGroup("GENERAL")
    @UIFieldValidationSize(min = 3, max = 100)
    public String getBasicTopic() {
        return getJsonData("bt", "zigbee2mqtt");
    }

    public void setBasicTopic(String value) {
        if (value.length() < 3 || value.length() > 100) {
            throw new IllegalArgumentException(
                "BasicTopic size must be between 3..100. Actual size: " + value.length());
        }
        setJsonData("bt", value);
    }

    @UIField(order = 50, inlineEdit = true)
    @UIFieldGroup("GENERAL")
    public boolean isEnableWatchdog() {
        return getJsonData("wd", false);
    }

    @UIField(order = 1)
    @UIFieldGroup("ADVANCED")
    @UIFieldSlider(min = 1, max = 60, header = "min.")
    public int getAvailabilityActiveTimeout() {
        return getJsonData("aat", 10);
    }

    public void setAvailabilityActiveTimeout(Integer value) {
        setJsonData("aat", value, 10, 1, 60);
    }

    @UIField(order = 2)
    @UIFieldGroup("ADVANCED")
    @UIFieldSlider(min = 1, max = 48, header = "hours")
    public int getAvailabilityPassiveTimeout() {
        return getJsonData("apt", 25);
    }

    public void setAvailabilityPassiveTimeout(Integer value) {
        setJsonData("apt", value, 25, 1, 48);
    }

    public void setEnableWatchdog(boolean value) {
        setJsonData("wd", value);
    }

    @UIContextMenuAction(value = "ZIGBEE_START_SCAN",
                         icon = "fas fa-search-location",
                         iconColor = "#899343")
    public ActionResponseModel scan() {
        return getService().startScan();
    }

    @UIContextMenuAction(value = "RESTART",
                         icon = "fas fa-power-off",
                         iconColor = Color.RED)
    public ActionResponseModel restart() {
        return getService().restartZ2M();
    }

    @UIContextMenuAction(value = "REINSTALL",
                         icon = "fas fa-trash-can-arrow-up",
                         iconColor = Color.RED)
    public ActionResponseModel reinstall() {
        return getService().reinstallZ2M();
    }

    @Override
    public @NotNull Class<Z2MLocalCoordinatorService> getEntityServiceItemClass() {
        return Z2MLocalCoordinatorService.class;
    }

    @Override
    @SneakyThrows
    public @NotNull Z2MLocalCoordinatorService createService(@NotNull EntityContext entityContext) {
        return new Z2MLocalCoordinatorService(entityContext, this);
    }

    @Override
    public void logBuilder(EntityLogBuilder entityLogBuilder) {
        entityLogBuilder.addTopicFilterByEntityID("org.homio.addon.zigbee");
        entityLogBuilder.addTopic(Z2MLocalCoordinatorService.class);
    }

    /**
     * Check if need start/stop z2m service
     */
    public boolean deepEqual(Z2MLocalCoordinatorEntity o) {
        return Objects.hash(getEntityID(), getBasicTopic(), getPort(), isStart(), getRawMqttEntity()) ==
            Objects.hash(o.getEntityID(), o.getBasicTopic(), o.getPort(), o.isStart(), o.getRawMqttEntity());
    }

    @Override
    public @NotNull Map<String, Map<String, ? extends ZigBeeProperty>> getCoordinatorTree() {
        Map<String, Map<String, ? extends ZigBeeProperty>> map = new HashMap<>();
        for (Entry<String, Z2MDeviceService> entry : getService().getDeviceHandlers().entrySet()) {
            map.put(entry.getKey(), entry.getValue().getProperties());
        }
        return map;
    }

    @Override
    public @NotNull Collection<ZigBeeDeviceBaseEntity> getZigBeeDevices() {
        return getService().getDeviceHandlers().values().stream()
                           .map(Z2MDeviceService::getDeviceEntity).collect(Collectors.toList());
    }

    @Override
    public ZigBeeDeviceBaseEntity getZigBeeDevice(@NotNull String ieeeAddress) {
        Z2MDeviceService service = getService().getDeviceHandlers().get(ieeeAddress);
        return service == null ? null : service.getDeviceEntity();
    }

    @Override
    @UIField(order = 1, hideInEdit = true)
    public String getFirmwareVersion() {
        return zigbee2mqttGitHub.getInstalledVersion();
    }

    @Getter
    @NoArgsConstructor
    private static class ZigBeeCoordinatorDeviceEntity {

        @UIField(order = 1)
        @UIFieldInlineEntityWidth(35)
        private String ieeeAddress;

        @UIField(order = 2)
        @UIFieldLinkToEntity(ZigBeeDeviceBaseEntity.class)
        private String name;

        @UIField(order = 4)
        @UIFieldInlineEntityWidth(10)
        private int endpointsCount;

        public ZigBeeCoordinatorDeviceEntity(Z2MDeviceService deviceHandler) {
            Z2MDeviceModel z2MDeviceModel = deviceHandler.getDevice();
            this.ieeeAddress = z2MDeviceModel.getIeeeAddress();
            this.name = deviceHandler.getDeviceEntity().getEntityID() + "~~~" + z2MDeviceModel.getName();
            this.endpointsCount = z2MDeviceModel.getDefinition().getExposes().size();
        }
    }
}
