package org.touchhome.bundle.z2m.util;

import static java.lang.String.format;
import static org.touchhome.bundle.api.util.TouchHomeUtils.OBJECT_MAPPER;
import static org.touchhome.bundle.z2m.service.properties.Z2MPropertyLastUpdate.UPDATED;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.BINARY_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.COMPOSITE_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.ENUM_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.NUMBER_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.SWITCH_TYPE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.repository.GitHubProject;
import org.touchhome.bundle.api.repository.GitHubProject.ProjectUpdate;
import org.touchhome.bundle.api.ui.field.ProgressBar;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.item.UIColorPickerItemBuilder.ColorType;
import org.touchhome.bundle.api.ui.field.action.v1.item.UIInfoItemBuilder.InfoType;
import org.touchhome.bundle.api.ui.field.action.v1.item.UISelectBoxItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.layout.UILayoutBuilder;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.hquery.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.z2m.NodeJSDependencyExecutableInstaller;
import org.touchhome.bundle.z2m.service.Z2MProperty;
import org.touchhome.bundle.z2m.service.properties.Z2MPropertyColor;
import org.touchhome.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;

@Log4j2
public final class ZigBeeUtil {

    public static final Path ZIGBEE_2_MQTT_PATH = TouchHomeUtils.getInstallPath().resolve("zigbee2mqtt");

    /**
     * Properties market with defined color, icon, etc...
     */
    public static final Map<String, Z2MDevicePropertiesDTO> DEVICE_PROPERTIES;
    public static final GitHubProject zigbee2mqttGitHub = new GitHubProject("Koenkk", "zigbee2mqtt");
    /**
     * Contains model/icon/iconColor/some setting config i.e. occupancy_timeout min..max values
     */
    private static final Map<String, Z2MDeviceDefinitionDTO> DEVICE_DEFINITIONS;
    public static String installedVersion;

    static {
        try {
            DEVICE_DEFINITIONS = new HashMap<>();
            List<Z2MDeviceDefinitionDTO> devices = OBJECT_MAPPER.readValue(
                ZigBeeUtil.class.getClassLoader().getResource("zigbee-devices.json"),
                new TypeReference<>() {});
            for (Z2MDeviceDefinitionDTO node : devices) {
                for (String model : node.getModel()) {
                    ZigBeeUtil.DEVICE_DEFINITIONS.put(model, node);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static {
        try {
            List<Z2MDevicePropertiesDTO> z2MDevicePropertiesDTOMap = OBJECT_MAPPER.readValue(
                ZigBeeUtil.class.getClassLoader().getResource("zigbee-device-properties.json"),
                new TypeReference<>() {});
            DEVICE_PROPERTIES = new HashMap<>();
            for (Z2MDevicePropertiesDTO z2MDevicePropertiesDTO : z2MDevicePropertiesDTOMap) {
                DEVICE_PROPERTIES.put(z2MDevicePropertiesDTO.getName(), z2MDevicePropertiesDTO);
                if (z2MDevicePropertiesDTO.getAlias() != null) {
                    for (String alias : z2MDevicePropertiesDTO.getAlias()) {
                        DEVICE_PROPERTIES.put(alias, z2MDevicePropertiesDTO);
                    }
                    z2MDevicePropertiesDTO.setAlias(null);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void zigbeeScanStarted(
        @NotNull EntityContext entityContext,
        @NotNull String entityID,
        int duration,
        @NotNull Runnable onDurationTimedOutHandler,
        @NotNull Runnable stopScanHandler) {
        entityContext.ui().headerButtonBuilder("zigbee-scan-" + entityID).title("zigbee.action.stop_scan").border(1, "#899343").clickAction(() -> {
                         stopScanHandler.run();
                         return null;
                     })
                     .duration(duration)
                     .icon("fas fa-search-location", "#899343", false)
                     .build();

        entityContext.bgp().builder("zigbee-scan-killer-" + entityID).delay(Duration.ofSeconds(duration)).execute(() -> {
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
        Z2MDeviceDefinitionDTO z2MDeviceDefinitionDTO = DEVICE_DEFINITIONS.get(modelId);
        JsonNode options = z2MDeviceDefinitionDTO == null ? null : z2MDeviceDefinitionDTO.getOptions();
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

    @SneakyThrows
    public static void installOrUpdateZ2M(boolean update, ProgressBar progressBar, EntityContext entityContext, String version, ProjectUpdate projectUpdate) {
        Path zigbee2mqttPackagePath = ZIGBEE_2_MQTT_PATH.resolve("node_modules");
        // Path targetZipPath = TouchHomeUtils.getInstallPath().resolve("zigbee2mqtt.tar.gz");
        boolean binaryExists = Files.exists(zigbee2mqttPackagePath);

        boolean requireUpdate = update && !getInstalledVersion().equals(version);
        if (binaryExists && !requireUpdate) {
            return;
        }
        ZigBeeUtil.installedVersion = null;

        // backup configuration
        if (binaryExists) {
            projectUpdate.backup(Path.of("data")).deleteProject();
        }

        projectUpdate.download(version);

        NodeJSDependencyExecutableInstaller installer = entityContext.getBean(NodeJSDependencyExecutableInstaller.class);
        if (installer.isRequireInstallDependencies(entityContext, true)) {
            progressBar.progress(0, "install-nodejs");
            installer.installDependency(entityContext, progressBar);
        }
        progressBar.progress(0, "install-zigbee2mqtt");
        MachineHardwareRepository machineHardwareRepository = entityContext.getBean(MachineHardwareRepository.class);
        machineHardwareRepository.execute("npm ci --prefix " + ZIGBEE_2_MQTT_PATH + " --no-audit --no-optional --no-update-notifier --unsafe-perm", 600,
            progressBar);
        machineHardwareRepository.execute("npm run build --prefix " + ZIGBEE_2_MQTT_PATH, 600, progressBar);
        machineHardwareRepository.execute(
            "npm ci --prefix " + ZIGBEE_2_MQTT_PATH + " --no-audit --no-optional --no-update-notifier --only=production --unsafe-perm", 600, progressBar);

        // restore configuration
        if (binaryExists) {
            projectUpdate.restore(Path.of("data"));
        }
    }

    @SneakyThrows
    public static String getInstalledVersion() {
        if (installedVersion == null) {
            ObjectNode packageNode = OBJECT_MAPPER.readValue(Files.readString(ZIGBEE_2_MQTT_PATH.resolve("package.json")), ObjectNode.class);
            installedVersion = packageNode.get("version").asText();
        }
        return installedVersion;
    }

    private static int getPropertyOrder(@NotNull String name) {
        int order = Optional.ofNullable(DEVICE_PROPERTIES.get(name))
                            .map(Z2MDevicePropertiesDTO::getOrder)
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
                       .setAsButton("fas fa-kitchen-set", null, null);
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
            uiInputBuilder.addButton(entityID, "fas fa-play", "#eb0000", (entityContext, params) -> {
                property.fireAction(property.getExpose().getValues().get(0));
                return null;
            }).setText("");
        } else {
            uiInputBuilder.addSelectBox(entityID, (entityContext, params) -> {
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
