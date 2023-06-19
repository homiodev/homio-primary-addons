package org.homio.addon.z2m.util;

import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;
import static org.homio.api.util.CommonUtils.getErrorMessage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.file.PathUtils;
import org.homio.addon.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.service.properties.inline.Z2MPropertyInline;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextSetting;
import org.homio.api.EntityContextUI.NotificationBlockBuilder;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.Icon;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Curl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class Z2MPropertyConfigService {

    private static final Path ZIGBEE_DEFINITION_FILE = CommonUtils.getConfigPath().resolve("zigbee-devices.json");
    @Getter
    private final Map<String, Class<? extends Z2MProperty>> converters = new HashMap<>();
    private final EntityContext entityContext;
    /**
     * Properties market with defined color, icon, etc...
     */
    @Getter
    private Map<String, Z2MDevicePropertiesModel> deviceProperties;
    /**
     * Contains model/icon/iconColor/some setting config i.e. occupancy_timeout min..max values
     */

    private Map<String, Z2MDeviceDefinitionModel> deviceDefinitions;
    private long localConfigHash;
    @Getter
    private boolean equalServerConfig = true;

    @SneakyThrows
    public Z2MPropertyConfigService(EntityContext entityContext) {
        this.entityContext = entityContext;
        URL localZdFile = Objects.requireNonNull(ZigBeeUtil.class.getClassLoader().getResource("zigbee-devices.json"));
        if (!Files.exists(ZIGBEE_DEFINITION_FILE) || EntityContextSetting.isDevEnvironment()) {
            PathUtils.copy(localZdFile::openStream, ZIGBEE_DEFINITION_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
        localConfigHash = Files.size(ZIGBEE_DEFINITION_FILE);

        initConverters();
        readZigbeeDevices();
    }

    public void synConfigurationFile() {
        if (!equalServerConfig) {
            try {
                log.info("Download new z2m configuration file");
                Curl.download(getServerConfigurationFileURL(), ZIGBEE_DEFINITION_FILE);
                localConfigHash = Files.size(ZIGBEE_DEFINITION_FILE);
                equalServerConfig = true;
                readZigbeeDevices();
                for (Z2MLocalCoordinatorEntity entity : entityContext.findAll(Z2MLocalCoordinatorEntity.class)) {
                    entity.getService().updateNotificationBlock();
                }
                log.info("New z2m configuration file downloaded");
                entityContext.ui().sendConfirmation("ZIGBEE.RESTART", "ZIGBEE.RESTART", () -> {
                    for (Z2MLocalCoordinatorEntity entity : entityContext.findAll(Z2MLocalCoordinatorEntity.class)) {
                        entity.getService().dispose(null);
                        entity.getService().restartCoordinator();
                    }
                }, List.of("ZIGBEE.RESTART_REQUIRE_ON_CONFIG"), null);
            } catch (Exception ex) {
                log.warn("Unable to reload z2m configuration file: {}", getErrorMessage(ex));
            }
        }
    }

    public void addUpdateButton(NotificationBlockBuilder builder) {
        if (!isEqualServerConfig()) {
            builder.addInfo("ZIGBEE.CONFIG_OUTDATED", null).setRightButton(new Icon("fas fa-download"), "UPDATE",
                "W.CONFIRM.Z2M_DOWNLOAD_CONFIG", (ec, params) -> {
                    synConfigurationFile();
                    return ActionResponseModel.fired();
                });
        }
    }

    public @NotNull String getDeviceIcon(@NotNull String modelId, @NotNull String defaultIcon) {
        return deviceDefinitions.containsKey(modelId) ? deviceDefinitions.get(modelId).getIcon() : defaultIcon;
    }

    public @NotNull String getDeviceIconColor(@NotNull String modelId, @NotNull String defaultIconColor) {
        return deviceDefinitions.containsKey(modelId) ? deviceDefinitions.get(modelId).getIconColor() : defaultIconColor;
    }

    public @NotNull List<WidgetDefinition> getDeviceWidgets(@NotNull String modelId) {
        List<WidgetDefinition> list = null;
        if (deviceDefinitions.containsKey(modelId)) {
            list = deviceDefinitions.get(modelId).getWidgets();
        }
        return list == null ? Collections.emptyList() : list;
    }

    public @NotNull JsonNode getDeviceOptions(@NotNull String modelId) {
        Z2MDeviceDefinitionModel z2MDeviceDefinitionModel = deviceDefinitions.get(modelId);
        JsonNode options = z2MDeviceDefinitionModel == null ? null : z2MDeviceDefinitionModel.getOptions();
        ObjectNode empty = OBJECT_MAPPER.createObjectNode();
        return options == null ? empty : options;
    }

    public int getPropertyOrder(@NotNull String name) {
        int order = Optional.ofNullable(deviceProperties.get(name))
                            .map(Z2MDevicePropertiesModel::getOrder)
                            .orElse(0);
        if (order == 0) {
            order = name.charAt(0) * 10 + name.charAt(1);
        }
        return order;
    }

    public void checkConfiguration() {
        if (equalServerConfig) {
            long serverConfigHash = Curl.getFileSize(getServerConfigurationFileURL());
            if (serverConfigHash != localConfigHash) {
                equalServerConfig = false;
            }
        }
    }

    @SneakyThrows
    public void readZigbeeDevices() {
        Z2MDeviceDefinitionsModel deviceConfigurations = OBJECT_MAPPER.readValue(ZIGBEE_DEFINITION_FILE.toFile(), Z2MDeviceDefinitionsModel.class);

        var definitions = new HashMap<String, Z2MDeviceDefinitionModel>();
        for (Z2MDeviceDefinitionModel node : deviceConfigurations.getDevices()) {
            for (String model : node.getModel()) {
                definitions.put(model, node);
            }
        }

        var properties = new HashMap<String, Z2MDevicePropertiesModel>();
        for (Z2MDevicePropertiesModel z2MDevicePropertiesModel : deviceConfigurations.getProperties()) {
            properties.put(z2MDevicePropertiesModel.getName(), z2MDevicePropertiesModel);
            if (z2MDevicePropertiesModel.getAlias() != null) {
                for (String alias : z2MDevicePropertiesModel.getAlias()) {
                    properties.put(alias, z2MDevicePropertiesModel);
                }
                z2MDevicePropertiesModel.setAlias(null);
            }
        }

        deviceDefinitions = definitions;
        deviceProperties = properties;
    }

    private void initConverters() {
        List<Class<? extends Z2MProperty>> z2mClusters = entityContext.getClassesWithParent(Z2MProperty.class);
        for (Class<? extends Z2MProperty> z2mCluster : z2mClusters) {
            Z2MProperty z2MProperty = CommonUtils.newInstance(z2mCluster);
            if (!Z2MPropertyInline.class.isAssignableFrom(z2mCluster)) {
                converters.put(z2MProperty.getPropertyDefinition(), z2mCluster);
            }
        }
    }

    private String getServerConfigurationFileURL() {
        return entityContext.setting().getEnvRequire("zigbee2mqtt-devices-uri");
    }
}
