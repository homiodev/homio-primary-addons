package org.homio.addon.z2m.util;

import static org.homio.addon.z2m.util.ApplianceModel.BINARY_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.COMPOSITE_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.ENUM_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.NUMBER_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.SWITCH_TYPE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.addon.z2m.service.endpoints.Z2MDeviceEndpointColor;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextHardware;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.Icon;
import org.homio.api.model.OptionModel;
import org.homio.api.repository.GitHubProject;
import org.homio.api.repository.GitHubProject.ProjectUpdate;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.ui.field.action.v1.item.UIColorPickerItemBuilder.ColorType;
import org.homio.api.ui.field.action.v1.item.UIInfoItemBuilder.InfoType;
import org.homio.api.ui.field.action.v1.item.UISelectBoxItemBuilder;
import org.homio.api.ui.field.action.v1.layout.UILayoutBuilder;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Lang;
import org.homio.hquery.ProgressBar;
import org.jetbrains.annotations.NotNull;

@Log4j2
public final class ZigBeeUtil {

    public static final GitHubProject zigbee2mqttGitHub =
            GitHubProject.of("Koenkk", "zigbee2mqtt",
                    CommonUtils.getInstallPath().resolve("zigbee2mqtt"));

    public static void zigbeeScanStarted(
            @NotNull EntityContext entityContext,
            @NotNull String entityID,
            int duration,
            @NotNull Runnable onDurationTimedOutHandler,
            @NotNull Runnable stopScanHandler) {
        entityContext.ui().headerButtonBuilder("zigbee-scan-" + entityID)
                .title("ZIGBEE_STOP_SCAN").border(1, "#899343").clickAction(() -> {
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

    public static @NotNull UIInputBuilder createActionBuilder(@NotNull Z2MDeviceEndpoint endpoint) {
        String entityID = endpoint.getEntityID();
        UIInputBuilder uiInputBuilder = endpoint.getDeviceService().getEntityContext().ui().inputBuilder();

        if (endpoint.isWritable()) {
            switch (endpoint.getExpose().getType()) {
                case ENUM_TYPE:
                    // corner case for smoke sensor, access=2 send selftest with empty string,
                    return buildWritableEnumTypeAction(endpoint, uiInputBuilder);
                case NUMBER_TYPE:
                    if (buildWritableNumberTypeAction(endpoint, uiInputBuilder)) {
                        return uiInputBuilder;
                    }
                    break;
                case SWITCH_TYPE:
                case BINARY_TYPE:
                    uiInputBuilder.addCheckbox(entityID, endpoint.getValue().boolValue(), (entityContext, params) -> {
                        endpoint.fireAction(params.getBoolean("value"));
                        return null;
                    }).setDisabled(!endpoint.getDevice().getStatus().isOnline());
                    return uiInputBuilder;
                case COMPOSITE_TYPE:
                    if (endpoint instanceof Z2MDeviceEndpointColor) {
                        uiInputBuilder
                                .addColorPicker(entityID, ((Z2MDeviceEndpointColor) endpoint).getStateColor(),
                                        (entityContext, params) -> {
                                            endpoint.fireAction(params.getString("value"));
                                            return null;
                                        })
                                .setColorType(ColorType.ColorSlider)
                                .setDisabled(!endpoint.getDevice().getStatus().isOnline());
                        return uiInputBuilder;
                    }
                default:
                    log.error("[{}]: Z2M write handler not implemented for device: {}, endpoint: {}",
                            endpoint.getDeviceService().getCoordinatorEntity().getEntityID(),
                            endpoint.getDeviceID(),
                            endpoint.getEndpointEntityID());
            }
        }
        if (endpoint.getUnit() != null) {
            uiInputBuilder.addInfo("%s <small class=\"text-muted\">%s</small>".formatted(endpoint.getValue().stringValue(), endpoint.getUnit()), InfoType.HTML);
        }
        endpoint.assembleUIAction(uiInputBuilder);
        return uiInputBuilder;
    }

    public static boolean isZ2MInstalled() {
        Path zigbee2mqttPackagePath = zigbee2mqttGitHub.getLocalProjectPath().resolve("node_modules");
        return Files.exists(zigbee2mqttPackagePath);
    }

    @SneakyThrows
    public static void installOrUpdateZ2M(
            @NotNull EntityContext entityContext,
            @NotNull String version,
            @NotNull ProjectUpdate projectUpdate) {
        ProgressBar progressBar = projectUpdate.getProgressBar();

        try {
            projectUpdate.getGitHubProject().deleteProject();
        } catch (Exception ex) {
            entityContext.ui().sendErrorMessage(
                Lang.getServerMessage("ZIGBEE.ERROR.DELETE",
                    projectUpdate.getGitHubProject().getLocalProjectPath().toString()));
            throw ex;
        }

        progressBar.progress(35, "Download sources");
        projectUpdate.downloadSource(version);

        String npm = entityContext.install().nodejs().requireSync(progressBar, null).getPath("npm");
        progressBar.progress(45, "install-zigbee2mqtt");
        EntityContextHardware hardware = entityContext.hardware();
        String npmOptions = "--no-audit --no-optional --no-update-notifier --unsafe-perm";
        hardware.execute("%s ci --prefix %s %s".formatted(npm, zigbee2mqttGitHub.getLocalProjectPath(), npmOptions), 600, progressBar);
        hardware.execute("%s run build --prefix %s".formatted(npm, zigbee2mqttGitHub.getLocalProjectPath()), 600, progressBar);
        hardware.execute("%s ci --prefix %s --only=production %s".formatted(npm, zigbee2mqttGitHub.getLocalProjectPath(), npmOptions), 600, progressBar);

        // restore configuration
        if (projectUpdate.isHasBackup()) {
            projectUpdate.copyFromBackup(Set.of(Path.of("data")));
        }
        progressBar.progress(100, "Zigbee2mqtt 'V%s' has been installed successfully".formatted(version));
    }

    public static @NotNull String getZ2MVersionToInstall(EntityContext entityContext) {
        String version = entityContext.setting().getEnv("zigbee2mqtt-version");
        if (StringUtils.isEmpty(version)) {
            version = zigbee2mqttGitHub.getLastReleaseVersion();
        }
        return Objects.requireNonNull(version);
    }

    @SneakyThrows
    public static void installZ2M(EntityContext entityContext) {
        String version = getZ2MVersionToInstall(entityContext);
        if (version.equals(zigbee2mqttGitHub.getInstalledVersion(entityContext))) {
            return;
        }

        entityContext.bgp().runWithProgress("install-z2m").executeSync(progressBar -> {
            zigbee2mqttGitHub.updateProject("z2m", progressBar, true, projectUpdate -> {
                ZigBeeUtil.installOrUpdateZ2M(entityContext, version, projectUpdate);
                return null;
            }, null);
        }).get(10, TimeUnit.MINUTES);
    }

    /**
     * Build action for 'numeric' type.
     */
    private static boolean buildWritableNumberTypeAction(
            @NotNull Z2MDeviceEndpoint endpoint,
            @NotNull UIInputBuilder uiInputBuilder) {

        String entityID = endpoint.getEntityID();
        if (endpoint.getExpose().getValueMin() != null && endpoint.getExpose().getValueMax() != null) {
            // create only slider if expose has only valueMin and valueMax
            if (endpoint.getExpose().getPresets() == null || endpoint.getExpose().getPresets().isEmpty()) {
                addUISlider(endpoint, uiInputBuilder, entityID);
                return true;
            }

            // build flex(selectBox,slider)
            uiInputBuilder.addFlex(entityID + "_compose", flex -> {
                UISelectBoxItemBuilder presets = flex.addSelectBox(entityID + "_presets", (entityContext, params) -> {
                    endpoint.fireAction(params.getInt("value"));
                    return null;
                }).setDisabled(!endpoint.getDevice().getStatus().isOnline());
                presets.addOptions(endpoint.getExpose().getPresets().stream().map(p ->
                                OptionModel.of(String.valueOf(p.getValue()), p.getName()).setDescription(p.getDescription())).collect(Collectors.toList()))
                        .setAsButton(new Icon("fas fa-kitchen-set"), null);
                // set selected presets if any presets equal to current value
                if (endpoint.getExpose().getPresets().stream().anyMatch(p -> String.valueOf(p.getValue()).equals(endpoint.getValue().toString()))) {
                    presets.setSelected(endpoint.getValue().toString());
                }

                addUISlider(endpoint, flex, entityID);
            });

            return true;
        }
        return false;
    }

    private static UIInputBuilder buildWritableEnumTypeAction(
            @NotNull Z2MDeviceEndpoint endpoint,
            @NotNull UIInputBuilder uiInputBuilder) {
        if (!endpoint.getExpose().isReadable() && endpoint.getExpose().getValues().size() == 1) {
            uiInputBuilder.addButton(endpoint.getEntityID(), new Icon("fas fa-play", "#eb0000"),
                    (entityContext, params) -> {
                        endpoint.fireAction(endpoint.getExpose().getValues().iterator().next());
                        return null;
                    }).setText("").setDisabled(!endpoint.getDevice().getStatus().isOnline());
        } else {
            uiInputBuilder
                    .addSelectBox(endpoint.getEntityID(), (entityContext, params) -> {
                        endpoint.fireAction(params.getString("value"));
                        return null;
                    })
                    .addOptions(OptionModel.list(endpoint.getExpose().getValues()))
                    .setPlaceholder("-----------")
                    .setSelected(endpoint.getValue().toString())
                    .setDisabled(!endpoint.getDevice().getStatus().isOnline());
        }
        return uiInputBuilder;
    }

    private static void addUISlider(
            @NotNull Z2MDeviceEndpoint endpoint,
            @NotNull UILayoutBuilder builder,
            @NotNull String entityID) {
        Objects.requireNonNull(endpoint.getExpose().getValueMin());
        Objects.requireNonNull(endpoint.getExpose().getValueMax());

        builder.addSlider(entityID,
                        endpoint.getValue().floatValue(0),
                        endpoint.getExpose().getValueMin().floatValue(),
                        endpoint.getExpose().getValueMax().floatValue(),
                        (entityContext, params) -> {
                            endpoint.fireAction(params.getInt("value"));
                            return null;
                        })
                .setDisabled(!endpoint.getDevice().getStatus().isOnline());
    }
}
