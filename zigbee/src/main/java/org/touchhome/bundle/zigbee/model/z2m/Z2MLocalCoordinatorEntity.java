package org.touchhome.bundle.zigbee.model.z2m;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.types.MicroControllerBaseEntity;
import org.touchhome.bundle.api.entity.types.StorageEntity;
import org.touchhome.bundle.api.entity.validation.UIFieldValidationSize;
import org.touchhome.bundle.api.exception.ProhibitedExecution;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.ui.UISidebarChildren;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldLinkToEntity;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntities;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntityWidth;
import org.touchhome.bundle.api.ui.field.selection.UIFieldEntityByClassSelection;
import org.touchhome.bundle.mqtt.entity.MQTTBaseEntity;
import org.touchhome.bundle.zigbee.model.ZigBeeBaseCoordinatorEntity;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceBaseEntity;
import org.touchhome.bundle.zigbee.service.z2m.Z2MDeviceService;
import org.touchhome.bundle.zigbee.service.z2m.Z2MLocalCoordinatorService;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO;

@Log4j2
@Entity
@UISidebarChildren(icon = "fas fa-circle-nodes", color = "#11A4C2")
public class Z2MLocalCoordinatorEntity extends MicroControllerBaseEntity<Z2MLocalCoordinatorEntity>
    implements ZigBeeBaseCoordinatorEntity<Z2MLocalCoordinatorEntity, Z2MLocalCoordinatorService> {

    public static final String PREFIX = "zb2mqtt_";

    @UIField(order = 9999, disableEdit = true, hideInEdit = true)
    @UIFieldInlineEntities(bg = "#27FF000D")
    public List<ZigBeeCoordinatorDeviceEntity> getCoordinatorDevices() {
        return optService().map(service -> service.getDeviceHandlers().values().stream().map(ZigBeeCoordinatorDeviceEntity::new).collect(Collectors.toList()))
                           .orElse(Collections.emptyList());
    }

    @UIField(order = 20, required = true, inlineEditWhenEmpty = true)
    @UIFieldEntityByClassSelection(MQTTBaseEntity.class)
    @UIFieldLinkToEntity(StorageEntity.class)
    public String getMqttEntity() {
        String value = getJsonData("mqtt");
        if (isNotEmpty(value)) {
            BaseEntity entity = getEntityContext().getEntity(value);
            if (entity instanceof MQTTBaseEntity) {
                return entity.getEntityID() + "~~~" + entity.getTitle();
            }
        }
        return value;
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
    public String getEntityPrefix() {
        return PREFIX;
    }

    @UIField(order = 30, inlineEdit = true)
    @UIFieldGroup("General")
    public boolean isPermitJoin() {
        return getJsonData("pj", false);
    }

    public void setPermitJoin(boolean value) {
        setJsonData("pj", value);
    }

    @UIField(order = 35, isRevert = true)
    @UIFieldGroup("General")
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

    @UIContextMenuAction(
        value = "zigbee.action.start_scan",
        icon = "fas fa-search-location",
        iconColor = "#899343")
    public ActionResponseModel scan() {
        return getService().startScan();
    }

    @UIContextMenuAction(
        value = "ACTION.COMMUNICATOR.RESTART",
        icon = "fas fa-power-off",
        iconColor = "#AB2A0A")
    public ActionResponseModel restart() {
        return getService().restart();
    }

    @Override
    public Class<Z2MLocalCoordinatorService> getEntityServiceItemClass() {
        return Z2MLocalCoordinatorService.class;
    }

    @Override
    @SneakyThrows
    public @NotNull Z2MLocalCoordinatorService createService(@NotNull EntityContext entityContext) {
        return new Z2MLocalCoordinatorService(entityContext, this);
    }

    @Override
    public void logBuilder(EntityLogBuilder entityLogBuilder) {
        entityLogBuilder.addTopicFilterByEntityID("org.touchhome.bundle.zigbee");
        entityLogBuilder.addTopic(Z2MLocalCoordinatorService.class);
    }

    public boolean deepEqual(Z2MLocalCoordinatorEntity newEntity) {
        return this.getEntityID().equals(newEntity.getEntityID())
            && this.getBasicTopic().equals(newEntity.getBasicTopic())
            && this.getPort().equals(newEntity.getPort())
            && this.isStart() == newEntity.isStart()
            && Objects.equals(this.getRawMqttEntity(), newEntity.getRawMqttEntity());
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
            Z2MDeviceDTO z2MDeviceDTO = deviceHandler.getDevice();
            this.ieeeAddress = z2MDeviceDTO.getIeeeAddress();
            this.name = deviceHandler.getDeviceEntity().getEntityID() + "~~~" + z2MDeviceDTO.getName();
            this.endpointsCount = z2MDeviceDTO.getDefinition().getExposes().size();
        }
    }
}
