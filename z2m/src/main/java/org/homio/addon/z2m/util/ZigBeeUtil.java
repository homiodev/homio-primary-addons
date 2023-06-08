package org.homio.addon.z2m.util;

import static java.lang.String.format;
import static org.homio.addon.z2m.service.properties.Z2MPropertyLastUpdate.UPDATED;
import static org.homio.addon.z2m.util.Z2MDeviceModel.BINARY_TYPE;
import static org.homio.addon.z2m.util.Z2MDeviceModel.COMPOSITE_TYPE;
import static org.homio.addon.z2m.util.Z2MDeviceModel.ENUM_TYPE;
import static org.homio.addon.z2m.util.Z2MDeviceModel.NUMBER_TYPE;
import static org.homio.addon.z2m.util.Z2MDeviceModel.SWITCH_TYPE;
import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;
import static org.homio.api.util.CommonUtils.getErrorMessage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.service.properties.Z2MPropertyColor;
import org.homio.addon.z2m.service.properties.dynamic.Z2MDynamicProperty;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextHardware;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.Icon;
import org.homio.api.model.OptionModel;
import org.homio.api.repository.GitHubProject;
import org.homio.api.repository.GitHubProject.ProjectUpdate;
import org.homio.api.ui.field.ProgressBar;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.ui.field.action.v1.item.UIColorPickerItemBuilder.ColorType;
import org.homio.api.ui.field.action.v1.item.UIInfoItemBuilder.InfoType;
import org.homio.api.ui.field.action.v1.item.UISelectBoxItemBuilder;
import org.homio.api.ui.field.action.v1.layout.UILayoutBuilder;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Curl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public final class ZigBeeUtil {

    public static final Path ZIGBEE_2_MQTT_PATH = CommonUtils.getInstallPath().resolve("zigbee2mqtt");
    private static final Path ZIGBEE_DEFINITION_FILE = CommonUtils.getConfigPath().resolve("zigbee-devices.json");

    /**
     * Properties market with defined color, icon, etc...
     */
    public static Map<String, Z2MDevicePropertiesModel> DEVICE_PROPERTIES;
    public static final GitHubProject zigbee2mqttGitHub = GitHubProject.of("Koenkk", "zigbee2mqtt");
    /**
     * Contains model/icon/iconColor/some setting config i.e. occupancy_timeout min..max values
     */
    private static Map<String, Z2MDeviceDefinitionModel> DEVICE_DEFINITIONS;
    public static String installedVersion;

    public static Map<String, Class<? extends Z2MProperty>> z2mConverters = new HashMap<>();

    @Getter
    private static long zdFileSize;

    static {
        try {
            URL localZdFile = Objects.requireNonNull(ZigBeeUtil.class.getClassLoader().getResource("zigbee-devices.json"));
            if (!Files.exists(ZIGBEE_DEFINITION_FILE)) {
                PathUtils.copy(localZdFile::openStream, ZIGBEE_DEFINITION_FILE);
            }
            zdFileSize = Files.size(ZIGBEE_DEFINITION_FILE);
            readZigbeeDevices();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void reloadZdFileIfRequire(String uri) {
        try {
            if (zdFileSize != Curl.getFileSize(uri)) {
                log.info("Download new zb file");
                Curl.download(uri, ZIGBEE_DEFINITION_FILE);
                zdFileSize = Files.size(ZIGBEE_DEFINITION_FILE);
                readZigbeeDevices();
            } else {
                log.info("ZB file same size");
            }
        } catch (Exception ex) {
            log.warn("Unable to reload zd file: {}", getErrorMessage(ex));
        }
    }

    @SneakyThrows
    public static void readZigbeeDevices() {
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

        DEVICE_DEFINITIONS = definitions;
        DEVICE_PROPERTIES = properties;
    }

    public static void zigbeeScanStarted(
        @NotNull EntityContext entityContext,
        @NotNull String entityID,
        int duration,
        @NotNull Runnable onDurationTimedOutHandler,
        @NotNull Runnable stopScanHandler) {
        entityContext.ui().headerButtonBuilder("zigbee-scan-" + entityID)
                     .title("CONTEXT.ACTION.ZIGBEE_STOP_SCAN").border(1, "#899343").clickAction(() -> {
                         stopScanHandler.run();
                         return ActionResponseModel.showWarn("ZIGBEE.STOP_SCAN");
                     })
                     .duration(duration)
                     .icon(new Icon("fas fa-search-location", "#899343"))
                     .build();

        entityContext.bgp().builder("zigbee-scan-killer-" + entityID)
                     .delay(Duration.ofSeconds(duration)).execute(() -> {
                         log.info("[{}]: Scanning stopped", entityID);
                         onDurationTimedOutHandler.run();
                         entityContext.ui().removeHeaderButton("zigbee-scan-" + entityID);
                     });
    }

    public static @NotNull String getDeviceIcon(@NotNull String modelId, @NotNull String defaultIcon) {
        return DEVICE_DEFINITIONS.containsKey(modelId) ? DEVICE_DEFINITIONS.get(modelId).getIcon() : defaultIcon;
    }

    public static @NotNull String getDeviceIconColor(@NotNull String modelId, @NotNull String defaultIconColor) {
        return DEVICE_DEFINITIONS.containsKey(modelId) ? DEVICE_DEFINITIONS.get(modelId).getIconColor() : defaultIconColor;
    }

    public static @NotNull List<WidgetDefinition> getDeviceWidgets(@NotNull String modelId) {
        List<WidgetDefinition> list = null;
        if (DEVICE_DEFINITIONS.containsKey(modelId)) {
            list = DEVICE_DEFINITIONS.get(modelId).getWidgets();
        }
        return list == null ? Collections.emptyList() : list;
    }

    public static @NotNull JsonNode getDeviceOptions(@NotNull String modelId) {
        Z2MDeviceDefinitionModel z2MDeviceDefinitionModel = DEVICE_DEFINITIONS.get(modelId);
        JsonNode options = z2MDeviceDefinitionModel == null ? null : z2MDeviceDefinitionModel.getOptions();
        ObjectNode empty = OBJECT_MAPPER.createObjectNode();
        return options == null ? empty : options;
    }

    public static @NotNull UIInputBuilder buildZigbeeActions(@NotNull Z2MProperty property, @NotNull String entityID) {
        UIInputBuilder uiInputBuilder = property.getDeviceService().getEntityContext().ui().inputBuilder();
        if (property.isWritable()) {
            switch (property.getExpose().getType()) {
                case ENUM_TYPE:
                    // corner case for smoke sensor, access=2 send selftest with empty string,
                    return buildWritableEnumTypeAction(property, uiInputBuilder, entityID);
                case NUMBER_TYPE:
                    if (buildWritableNumberTypeAction(property, uiInputBuilder, entityID)) {
                        return uiInputBuilder;
                    }
                    break;
                case SWITCH_TYPE:
                case BINARY_TYPE:
                    uiInputBuilder.addCheckbox(entityID, property.getValue().boolValue(), (entityContext, params) -> {
                        property.fireAction(params.getBoolean("value"));
                        return null;
                    });
                    return uiInputBuilder;
                case COMPOSITE_TYPE:
                    if (property instanceof Z2MPropertyColor) {
                        uiInputBuilder.addColorPicker(entityID, ((Z2MPropertyColor) property).getStateColor(), (entityContext, params) -> {
                            property.fireAction(params.getString("value"));
                            return null;
                        }).setColorType(ColorType.ColorSlider);
                        return uiInputBuilder;
                    }
                default:
                    log.error("[{}]: Z2M write handler not implemented for device: {}, property: {}",
                        property.getDeviceService().getCoordinatorService().getEntityID(),
                        property.getDeviceService().getDeviceEntity().getEntityID(),
                        property.getExpose().getProperty());
            }
        }
        if (property.getUnit() != null) {
            uiInputBuilder.addInfo(format("%s <small class=\"text-muted\">%s</small>",
                property.getValue().stringValue(), property.getUnit()), InfoType.HTML);
        }
        if (UPDATED.equals(property.getExpose().getProperty())) {
            uiInputBuilder.addDuration(property.getValue().longValue(), null);
        } else {
            uiInputBuilder.addInfo(property.getValue().toString(), InfoType.HTML);
        }
        return uiInputBuilder;
    }

    public static String splitNameToReadableFormat(@NotNull String name) {
        String[] items = name.split("_");
        return StringUtils.capitalize(String.join(" ", items));
    }

    public static int compareProperty(@NotNull String name1, @NotNull String name2) {
        return Integer.compare(getPropertyOrder(name1), getPropertyOrder(name2));
    }

    public static boolean isZ2MInstalled() {
        Path zigbee2mqttPackagePath = ZIGBEE_2_MQTT_PATH.resolve("node_modules");
        return Files.exists(zigbee2mqttPackagePath);
    }

    @SneakyThrows
    public static void installOrUpdateZ2M(boolean update, ProgressBar progressBar, EntityContext entityContext, String version, ProjectUpdate projectUpdate) {
        boolean binaryExists = isZ2MInstalled();

        boolean requireUpdate = update && !getInstalledVersion().equals(version);
        if (binaryExists && !requireUpdate) {
            return;
        }
        ZigBeeUtil.installedVersion = null;

        // backup configuration
        Path dataPath = Path.of("data");
        if (binaryExists) {
            projectUpdate.backup(dataPath).deleteProject();
        }

        projectUpdate.downloadSource(version);

        String npm = entityContext.install().nodejs().requireSync(progressBar, null).getPath("npm");
        progressBar.progress(0, "install-zigbee2mqtt");
        EntityContextHardware hardware = entityContext.hardware();
        String npmOptions = "--no-audit --no-optional --no-update-notifier --unsafe-perm";
        hardware.execute(format("%s ci --prefix %s %s", npm, ZIGBEE_2_MQTT_PATH, npmOptions), 600, progressBar);
        hardware.execute(format("%s run build --prefix %s", npm, ZIGBEE_2_MQTT_PATH), 600, progressBar);
        hardware.execute(format("%s ci --prefix %s --only=production %s", npm, ZIGBEE_2_MQTT_PATH, npmOptions), 600, progressBar);

        // restore configuration
        if (binaryExists) {
            projectUpdate.restore(dataPath);
        }
    }

    @SneakyThrows
    public static String getInstalledVersion() {
        if (installedVersion == null) {
            try {
                ObjectNode packageNode = OBJECT_MAPPER.readValue(Files.readString(ZIGBEE_2_MQTT_PATH.resolve("package.json")), ObjectNode.class);
                installedVersion = packageNode.get("version").asText();
            } catch (Exception ex) {
                installedVersion = "-.-.-";
            }
        }
        return installedVersion;
    }

    public static synchronized void collectZ2MConverters(EntityContext entityContext) {
        if (z2mConverters == null) {
            z2mConverters = new HashMap<>();
            List<Class<? extends Z2MProperty>> z2mClusters = entityContext.getClassesWithParent(Z2MProperty.class);
            for (Class<? extends Z2MProperty> z2mCluster : z2mClusters) {
                if (!Z2MDynamicProperty.class.isAssignableFrom(z2mCluster)) {
                    Z2MProperty z2MProperty = CommonUtils.newInstance(z2mCluster);
                    z2mConverters.put(z2MProperty.getPropertyDefinition(), z2mCluster);
                }
            }
        }
    }

    @Nullable
    public static String getZ2MVersionToInstall(EntityContext entityContext) {
        String version = entityContext.setting().getEnv("zigbee2mqtt-version");
        if (StringUtils.isEmpty(version)) {
            version = zigbee2mqttGitHub.getLastReleaseVersion();
        }
        return version;
    }

    public static void installZ2M(EntityContext entityContext, Runnable runnable) {
        entityContext.bgp().runWithProgress("install-z2m").execute(progressBar -> {
            String version = getZ2MVersionToInstall(entityContext);
            zigbee2mqttGitHub.updating("z2m", ZIGBEE_2_MQTT_PATH, progressBar, projectUpdate -> {
                ZigBeeUtil.installOrUpdateZ2M(false, progressBar, entityContext, version, projectUpdate);
                return null;
            });
            runnable.run();
        });
    }

    private static int getPropertyOrder(@NotNull String name) {
        int order = Optional.ofNullable(DEVICE_PROPERTIES.get(name))
                            .map(Z2MDevicePropertiesModel::getOrder)
                            .orElse(0);
        if (order == 0) {
            order = name.charAt(0) * 10 + name.charAt(1);
        }
        return order;
    }

    /**
     * Build action for 'numeric' type.
     */
    private static boolean buildWritableNumberTypeAction(
        @NotNull Z2MProperty property,
        @NotNull UIInputBuilder uiInputBuilder,
        @NotNull String entityID) {
        if (property.getExpose().getValueMin() != null && property.getExpose().getValueMax() != null) {
            // create only slider if expose has only valueMin and valueMax
            if (property.getExpose().getPresets() == null || property.getExpose().getPresets().isEmpty()) {
                addUISlider(property, uiInputBuilder, entityID);
                return true;
            }

            // build flex(selectBox,slider)
            uiInputBuilder.addFlex(entityID + "_compose", flex -> {
                UISelectBoxItemBuilder presets = flex.addSelectBox(entityID + "_presets", (entityContext, params) -> {
                    property.fireAction(params.getInt("value"));
                    return null;
                });
                presets.addOptions(property.getExpose().getPresets().stream().map(p ->
                           OptionModel.of(String.valueOf(p.getValue()), p.getName()).setDescription(p.getDescription())).collect(Collectors.toList()))
                       .setAsButton(new Icon("fas fa-kitchen-set"), null);
                // set selected presets if any presets equal to current value
                if (property.getExpose().getPresets().stream().anyMatch(p -> String.valueOf(p.getValue()).equals(property.getValue().toString()))) {
                    presets.setSelected(property.getValue().toString());
                }

                addUISlider(property, flex, entityID);
            });

            return true;
        }
        return false;
    }

    private static UIInputBuilder buildWritableEnumTypeAction(
        @NotNull Z2MProperty property,
        @NotNull UIInputBuilder uiInputBuilder,
        @NotNull String entityID) {
        if (!property.getExpose().isReadable() && property.getExpose().getValues().size() == 1) {
            uiInputBuilder.addButton(entityID, new Icon("fas fa-play", "#eb0000"),
                (entityContext, params) -> {
                    property.fireAction(property.getExpose().getValues().get(0));
                    return null;
                }).setText("");
        } else {
            uiInputBuilder
                .addSelectBox(entityID, (entityContext, params) -> {
                    property.fireAction(params.getString("value"));
                    return null;
                })
                .addOptions(OptionModel.list(property.getExpose().getValues()))
                .setPlaceholder("-----------")
                .setSelected(property.getValue().toString());
        }
        return uiInputBuilder;
    }

    private static void addUISlider(
        @NotNull Z2MProperty property,
        @NotNull UILayoutBuilder builder,
        @NotNull String entityID) {
        Objects.requireNonNull(property.getExpose().getValueMin());
        Objects.requireNonNull(property.getExpose().getValueMax());

        builder.addSlider(entityID,
            property.getValue().floatValue(0),
            property.getExpose().getValueMin().floatValue(),
            property.getExpose().getValueMax().floatValue(),
            (entityContext, params) -> {
                property.fireAction(params.getInt("value"));
                return null;
            });
    }
}
