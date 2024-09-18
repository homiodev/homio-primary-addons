package org.homio.addon.z2m.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.Z2MEntrypoint;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.Z2MLocalCoordinatorService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.api.Context;
import org.homio.api.ContextService;
import org.homio.api.ContextService.MQTTEntityService;
import org.homio.api.entity.log.HasEntitySourceLog;
import org.homio.api.entity.types.CommunicationEntity;
import org.homio.api.entity.types.MicroControllerBaseEntity;
import org.homio.api.entity.validation.UIFieldValidationSize;
import org.homio.api.entity.version.HasGitHubFirmwareVersion;
import org.homio.api.entity.zigbee.ZigBeeBaseCoordinatorEntity;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.OptionModel;
import org.homio.api.model.endpoint.DeviceEndpoint;
import org.homio.api.repository.GitHubProject;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.UISidebarChildren;
import org.homio.api.ui.field.*;
import org.homio.api.ui.field.action.UIContextMenuAction;
import org.homio.api.ui.field.color.UIFieldColorRef;
import org.homio.api.ui.field.inline.UIFieldInlineEntities;
import org.homio.api.ui.field.inline.UIFieldInlineEntityWidth;
import org.homio.api.ui.field.selection.UIFieldEntityTypeSelection;
import org.homio.api.util.DataSourceUtil;
import org.homio.api.util.DataSourceUtil.SelectionSource;
import org.homio.api.util.HardwareUtils;
import org.homio.api.util.Lang;
import org.homio.hquery.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.homio.addon.z2m.util.ZigBeeUtil.zigbee2mqttGitHub;
import static org.homio.api.ui.UI.Color.ERROR_DIALOG;

@SuppressWarnings({"unused"})
@Log4j2
@Entity
@UISidebarChildren(icon = "fas fa-circle-nodes", color = "#11A4C2")
public class Z2MLocalCoordinatorEntity extends MicroControllerBaseEntity
        implements
        HasGitHubFirmwareVersion,
        HasEntitySourceLog,
        ZigBeeBaseCoordinatorEntity<Z2MLocalCoordinatorEntity, Z2MLocalCoordinatorService> {

    @UIField(order = 9999, disableEdit = true, hideInEdit = true)
    @UIFieldInlineEntities(bg = "#27FF000D")
    public List<ZigBeeCoordinatorDeviceEntity> getCoordinatorDevices() {
        return optService()
                .map(service ->
                        service.getDeviceHandlers().values()
                                .stream()
                                .sorted()
                                .map(ZigBeeCoordinatorDeviceEntity::new)
                                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @UIField(order = 20, required = true, inlineEditWhenEmpty = true)
    @UIFieldEntityTypeSelection(type = ContextService.MQTT_SERVICE)
    @UIFieldLinkToEntity(value = CommunicationEntity.class, applyTitle = true)
    public String getMqttEntity() {
        return getJsonData("mqtt");
    }

    public void setMqttEntity(String value) {
        setJsonData("mqtt", value);
    }

    @JsonIgnore
    public @Nullable MQTTEntityService getMqttEntityService() {
        SelectionSource selection = DataSourceUtil.getSelection(getMqttEntity());
        return selection.getValue(context());
    }

    @Override
    public String getDefaultName() {
        return "ZigBee2MQTT";
    }

    @Override
    protected void assembleMissingMandatoryFields(@NotNull Set<String> fields) {
        if (getMqttEntity().isEmpty()) {
            fields.add("mqttEntity");
        }
    }

    @Override
    public @Nullable Set<String> getConfigurationErrors() {
        Set<String> errors = new HashSet<>();
        try {
            if (getMqttEntityService() == null) {
                errors.add("ERROR.NO_MQTT_SELECTED");
            }

        } catch (Exception ex) {
            errors.add("ERROR.WRONG_MQTT_BROKER");
        }
        if (getPort().isEmpty()) {
            errors.add("ERROR.NO_PORT_SELECTED");
        } else if (HardwareUtils.getSerialPort(getPort()) == null) {
            errors.add(Lang.getServerMessage("ZIGBEE.ERROR.PORT_NOT_FOUND", getPort()));
        }
        return errors;
    }

    @Override
    public long getEntityServiceHashCode() {
        return getJsonDataHashCode("mqtt", "bt", "port", "start", "rt");
    }

    public void setRestartToken(long id) {
        setJsonData("rt", id);
    }

    @Override
    protected @NotNull String getDevicePrefix() {
        return "zb2mqtt";
    }

    @UIField(order = 30, inlineEdit = true)
    @UIFieldGroup("GENERAL")
    public boolean isPermitJoin() {
        return getJsonData("pj", false);
    }

    public void setPermitJoin(boolean value) {
        setJsonData("pj", value);
    }

    @UIField(order = 35)
    @UIFieldReadDefaultValue
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

    @UIField(order = 60, hideInEdit = true, hideOnEmpty = true)
    @UIFieldGroup("GENERAL")
    public Boolean isRunLocally() {
        if (getStatus().isOnline()) {
            return getService().isRunningLocally();
        }
        return null;
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

    @UIField(order = 3, type = UIFieldType.Chips)
    @UIFieldGroup("ADVANCED")
    public Set<String> getHiddenEndpoints() {
        return getJsonDataSet("he");
    }

    public void setHiddenEndpoints(String value) {
        setJsonData("he", value);
    }

    @UIContextMenuAction(value = "ZIGBEE_START_SCAN",
            icon = "fas fa-search-location",
            iconColor = "#899343")
    public ActionResponseModel scan() {
        return getService().startScan();
    }

    @UIContextMenuAction(value = "RESTART",
            icon = "fas fa-power-off",
            confirmMessage = "W.CONFIRM.ZIGBEE_RESTART",
            confirmMessageDialogColor = "#4E481E",
            iconColor = Color.RED)
    public ActionResponseModel restart() {
        return getService().restartZ2M();
    }

    @UIContextMenuAction(value = "REINSTALL",
            icon = "fas fa-trash-can-arrow-up",
            confirmMessage = "W.CONFIRM.Z2M_REINSTALL",
            confirmMessageDialogColor = ERROR_DIALOG,
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
    public @NotNull Z2MLocalCoordinatorService createService(@NotNull Context context) {
        return new Z2MLocalCoordinatorService(context, this);
    }

    @Override
    public void logBuilder(EntityLogBuilder entityLogBuilder) {
        entityLogBuilder.addTopicFilterByEntityID(Z2MEntrypoint.class.getPackage());
    }

    @Override
    @SneakyThrows
    public @NotNull InputStream getSourceLogInputStream(@NotNull String sourceID) {
        return Files.newInputStream(zigbee2mqttGitHub.getLocalProjectPath().resolve("data/log/log.txt"));
    }

    @Override
    public @NotNull List<OptionModel> getLogSources() {
        return List.of(OptionModel.of("log", "Zigbee2mqtt Log File"));
    }

    @Override
    public @NotNull Map<String, Map<String, ? extends DeviceEndpoint>> getCoordinatorTree() {
        Map<String, Map<String, ? extends DeviceEndpoint>> map = new HashMap<>();
        for (Entry<String, Z2MDeviceService> entry : getService().getDeviceHandlers().entrySet()) {
            map.put(entry.getKey(), entry.getValue().getEndpoints());
        }
        return map;
    }

    @Override
    public @NotNull Collection<ZigBeeDeviceBaseEntity> getZigBeeDevices() {
        return getService().getDeviceHandlers().values().stream()
                .map(Z2MDeviceService::getDeviceEntity).collect(Collectors.toList());
    }

    @Override
    public Z2MDeviceEntity getZigBeeDevice(@NotNull String ieeeAddress) {
        Z2MDeviceService service = getService().getDeviceHandlers().get(ieeeAddress);
        return service == null ? null : service.getDeviceEntity();
    }

    @Override
    public void beforePersist() {
        setHiddenEndpoints("update_available%sdevice_status".formatted(LIST_DELIMITER));
    }

    @Override
    public @NotNull GitHubProject getGitHubProject() {
        return zigbee2mqttGitHub;
    }

    @Override
    public ActionResponseModel update(@NotNull ProgressBar progressBar, @NotNull String version) {
        return getService().updateFirmware(progressBar, version);
    }

    @Getter
    @NoArgsConstructor
    public static class ZigBeeCoordinatorDeviceEntity {

        @UIField(order = 1)
        @UIFieldInlineEntityWidth(35)
        @UIFieldLinkToEntity(ZigBeeDeviceBaseEntity.class)
        private String ieeeAddress;

        @UIField(order = 2)
        @UIFieldColorRef("color")
        private String name;

        @UIField(order = 4)
        @UIFieldInlineEntityWidth(10)
        private int endpointsCount;

        private String color;

        public ZigBeeCoordinatorDeviceEntity(Z2MDeviceService deviceHandler) {
            ApplianceModel applianceModel = deviceHandler.getApplianceModel();
            String address = applianceModel.getIeeeAddress();
            color = deviceHandler.getDeviceEntity().getStatus().getColor();
            name = deviceHandler.getDeviceEntity().getName();
            if (StringUtils.isEmpty(name) || name.equalsIgnoreCase(address)) {
                name = deviceHandler.getDeviceEntity().getDescription();
            }
            ieeeAddress = deviceHandler.getDeviceEntity().getIeeeAddress();
            endpointsCount = applianceModel.getDefinition().getExposes().size();
        }
    }
}
