package org.homio.addon.z2m.util;

import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;
import static org.homio.api.util.CommonUtils.getErrorMessage;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.file.PathUtils;
import org.homio.addon.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.addon.z2m.service.Z2MDeviceService;
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

    private long localConfigHash;
    @Getter
    private boolean equalServerConfig = true;
    @Getter
    private FileMeta fileMeta = new FileMeta();

    private final Map<String, ModelDevices> modelIdToDevices = new HashMap<>();
    private final ReentrantLock midLock = new ReentrantLock();

    @SneakyThrows
    public Z2MPropertyConfigService(EntityContext entityContext) {
        this.entityContext = entityContext;
        URL localZdFile = Objects.requireNonNull(ZigBeeUtil.class.getClassLoader().getResource("zigbee-devices.json"));
        if (!Files.exists(ZIGBEE_DEFINITION_FILE) || EntityContextSetting.isDevEnvironment()) {
            PathUtils.copy(localZdFile::openStream, ZIGBEE_DEFINITION_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
        localConfigHash = Files.size(ZIGBEE_DEFINITION_FILE);

        initConverters();
        fileMeta.readZigbeeDevices();
    }

    public void syncConfigurationFile() {
        if (!equalServerConfig) {
            try {
                log.info("Download new z2m configuration file");
                Curl.download(getServerConfigurationFileURL(), ZIGBEE_DEFINITION_FILE);
                // currently we use only hash to distinguish if file os newer
                localConfigHash = Files.size(ZIGBEE_DEFINITION_FILE);
                equalServerConfig = true;
                fileMeta.readZigbeeDevices();
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
                    syncConfigurationFile();
                    return ActionResponseModel.fired();
                });
        }
    }

    public @NotNull String getDeviceIcon(@NotNull Z2MDeviceService deviceService, @NotNull String defaultIcon) {
        List<Z2MDeviceDefinitionModel> devices = findDevices(deviceService);
        return devices.isEmpty() ? defaultIcon : devices.get(0).getIcon();
    }

    public @NotNull String getDeviceIconColor(@NotNull Z2MDeviceService deviceService, @NotNull String defaultIconColor) {
        List<Z2MDeviceDefinitionModel> devices = findDevices(deviceService);
        return devices.isEmpty() ? defaultIconColor : devices.get(0).getIconColor();
    }

    public @NotNull List<WidgetDefinition> getDeviceWidgets(Z2MDeviceService deviceService) {
        return findDevices(deviceService).stream().flatMap(d -> d.getWidgets().stream()).toList();
    }

    public @NotNull JsonNode getDeviceOptions(@NotNull Z2MDeviceService deviceService) {
        List<Z2MDeviceDefinitionModel> devices = findDevices(deviceService);
        JsonNode jsonNode = null;
        if (!devices.isEmpty()) {
            jsonNode = devices.get(0).getOptions();
        }
        return jsonNode == null ? OBJECT_MAPPER.createObjectNode() : jsonNode;
    }

    public int getPropertyOrder(@NotNull String name) {
        int order = Optional.ofNullable(fileMeta.deviceProperties.get(name))
                            .map(Z2MPropertyModel::getOrder)
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

    private void initConverters() {
        List<Class<? extends Z2MProperty>> z2mClusters = entityContext.getClassesWithParent(Z2MProperty.class);
        for (Class<? extends Z2MProperty> z2mCluster : z2mClusters) {
            if (!Z2MPropertyInline.class.isAssignableFrom(z2mCluster)) {
                Z2MProperty z2MProperty = CommonUtils.newInstance(z2mCluster);
                converters.put(z2MProperty.getPropertyDefinition(), z2mCluster);
            }
        }
    }

    private String getServerConfigurationFileURL() {
        return entityContext.setting().getEnvRequire("zigbee2mqtt-devices-uri");
    }

    private @NotNull List<Z2MDeviceDefinitionModel> findDevices(@NotNull Z2MDeviceService deviceService) {
        Set<String> exposes = deviceService.getExposes();
        int exposeHash = exposes.hashCode();
        String modelId = deviceService.getApplianceModel().getModelId();
        ModelDevices modelDevices = modelIdToDevices.get(modelId);
        if (modelDevices == null || modelDevices.hashCode != exposeHash) {
            try {
                midLock.lock();
                modelDevices = modelIdToDevices.get(modelId);
                if (modelDevices == null || modelDevices.hashCode != exposeHash) {
                    modelDevices = new ModelDevices(exposeHash, buildListOfDevices(modelId, exposes));
                    modelIdToDevices.put(modelId, modelDevices);
                }
            } finally {
                midLock.unlock();
            }
        }
        return modelDevices.devices;
    }

    private List<Z2MDeviceDefinitionModel> buildListOfDevices(String modelId, Set<String> exposes) {
        List<Z2MDeviceDefinitionModel> devices = new ArrayList<>();
        Z2MDeviceDefinitionModel device = fileMeta.deviceDefinitions.get(modelId);
        if (device != null) {
            devices.add(device);
        }
        for (Entry<ExposeMatch, List<Z2MDeviceDefinitionModel>> item : fileMeta.exposeDeviceDefinitions.entrySet()) {
            if (exposes.containsAll(item.getKey().andExposes)) {
                devices.addAll(item.getValue());
            }
        }
        return devices;
    }

    public static class FileMeta {

        /**
         * Properties market with defined color, icon, etc...
         */
        @Getter
        private @NotNull Map<String, Z2MPropertyModel> deviceProperties = Collections.emptyMap();
        @Getter
        private @NotNull Map<String, Z2MPropertyModel> deviceAliasProperties = Collections.emptyMap();
        @Getter
        private @NotNull Set<String> ignoreProperties = Collections.emptySet();
        @Getter
        private @NotNull Set<String> propertiesWithoutVariables = Collections.emptySet();
        @Getter
        private @NotNull Set<String> hiddenProperties = Collections.emptySet();
        /**
         * Contains model/icon/iconColor/some setting config i.e. occupancy_timeout min..max values
         */
        private @NotNull Map<String, Z2MDeviceDefinitionModel> deviceDefinitions = Collections.emptyMap();

        private @NotNull Map<ExposeMatch, List<Z2MDeviceDefinitionModel>> exposeDeviceDefinitions = Collections.emptyMap();

        @SneakyThrows
        public void readZigbeeDevices() {
            Z2MDeviceDefinitionsModel deviceConfigurations = OBJECT_MAPPER.readValue(ZIGBEE_DEFINITION_FILE.toFile(), Z2MDeviceDefinitionsModel.class);

            var definitions = new HashMap<String, Z2MDeviceDefinitionModel>();
            for (Z2MDeviceDefinitionModel node : deviceConfigurations.getDevices()) {
                if (node.getModels() != null) {
                    for (String model : node.getModels()) {
                        definitions.put(model, node);
                    }
                }
            }

            var exposeDefinitions = new HashMap<ExposeMatch, List<Z2MDeviceDefinitionModel>>();
            for (Z2MDeviceDefinitionModel node : deviceConfigurations.getDevices()) {
                if (node.getExposes() != null) {
                    for (String expose : node.getExposes()) {
                        ExposeMatch exposeMatch = new ExposeMatch(Stream.of(expose.split("~")).collect(Collectors.toSet()));
                        exposeDefinitions.putIfAbsent(exposeMatch, new ArrayList<>());
                        exposeDefinitions.get(exposeMatch).add(node);
                    }
                }
            }

            var aliasProperties = new HashMap<String, Z2MPropertyModel>();
            for (Z2MPropertyModel z2MPropertyModel : deviceConfigurations.getProperties()) {
                if (z2MPropertyModel.getAlias() != null) {
                    for (String alias : z2MPropertyModel.getAlias()) {
                        aliasProperties.put(alias, z2MPropertyModel);
                    }
                }
            }

            if (deviceConfigurations.getPropertiesWithoutVariables() != null) {
                ignoreProperties = deviceConfigurations.getPropertiesWithoutVariables();
            }
            if (deviceConfigurations.getHiddenProperties() != null) {
                hiddenProperties = deviceConfigurations.getHiddenProperties();
            }
            if (deviceConfigurations.getPropertiesWithoutVariables() != null) {
                propertiesWithoutVariables = deviceConfigurations.getPropertiesWithoutVariables();
            }

            exposeDeviceDefinitions = exposeDefinitions;
            deviceDefinitions = definitions;
            deviceProperties = deviceConfigurations.getProperties().stream()
                                                   .collect(Collectors.toMap(
                                                       Z2MPropertyModel::getName, Function.identity()));
            deviceAliasProperties = aliasProperties;
        }
    }

    @RequiredArgsConstructor
    public static final class ExposeMatch {

        // minimum of exposes to match
        private @NotNull final Set<String> andExposes;

        @Override
        public boolean equals(Object o) {
            if (this == o) {return true;}
            if (o == null || getClass() != o.getClass()) {return false;}

            ExposeMatch that = (ExposeMatch) o;

            return andExposes.equals(that.andExposes);
        }

        @Override
        public int hashCode() {
            return andExposes.hashCode();
        }
    }

    @AllArgsConstructor
    private static final class ModelDevices {

        private int hashCode;
        private List<Z2MDeviceDefinitionModel> devices;
    }
}
