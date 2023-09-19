package org.homio.addon.z2m.service.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.api.EntityContext;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.Icon;
import org.homio.api.state.JsonType;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.util.FlowMap;
import org.homio.api.util.Lang;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MDeviceEndpointFirmwareUpdate extends Z2MDeviceEndpoint {

    public static final String ENDPOINT_FIRMWARE_UPDATE = "update";

    private boolean wasProgress;

    public Z2MDeviceEndpointFirmwareUpdate(@NotNull EntityContext entityContext) {
        super(new Icon("fa fa-fw fa-tablets", "#FF0000"), entityContext);
        setValue(new JsonType("{}"), false);
    }

    @Override
    public void init(@NotNull Z2MDeviceService deviceService, @NotNull ApplianceModel.Z2MDeviceDefinition.Options expose) {
        expose.setType(ApplianceModel.COMPOSITE_TYPE);
        super.init(deviceService, expose);

        // listen for changes
        addChangeListener("internal", ignore -> {
            JsonNode node = ((JsonType) getValue()).getJsonNode();
            String status = node.get("state").asText();
            if ("updating".equals(status)) {
                wasProgress = true;
                String message = Lang.getServerMessage("ZIGBEE.UPDATING",
                        FlowMap.of("NAME", getDeviceService().getDeviceEntity().getTitle(), "VALUE", node.get("remaining").asInt()));
                getEntityContext().ui().progress("upd-" + getDeviceService().getIeeeAddress(),
                        node.get("progress").asDouble(), message, false);
            } else if (wasProgress) {
                getEntityContext().ui().progressDone("upd-" + getDeviceService().getIeeeAddress());
            }
        });
    }

    @Override
    public String getDescription() {
        JsonType value = (JsonType) getValue();
        String installedVersion = value.get("installed_version").asText(null);
        String latestVersion = value.get("latest_version").asText(null);
        if (installedVersion != null) {
            String text = "V" + installedVersion;
            if (!installedVersion.equals(latestVersion)) {
                text += "/V" + latestVersion;
            }
            return Lang.getServerMessage("ZIGBEE.UPDATE_DESCRIPTION", text);
        }
        return super.getDescription();
    }

    @Override
    public String getEndpointDefinition() {
        return ENDPOINT_FIRMWARE_UPDATE;
    }

    @Override
    public void assembleUIAction(@NotNull UIInputBuilder uiInputBuilder) {
        JsonNode node = ((JsonType) getValue()).getJsonNode();
        switch (node.get("state").asText()) {
            case "available" -> {
                String updateTitle = node.get("installed_version").asText() + "=>" + node.get("latest_version").asText();
                uiInputBuilder.addButton(getEntityID(), new Icon("fas fa-retweet", "#FF0000"),
                                (entityContext, params) -> sendRequest(Request.update))
                        .setText(updateTitle)
                        .setConfirmMessage("W.CONFIRM.ZIGBEE_UPDATE")
                        .setConfirmMessageDialogColor(Color.ERROR_DIALOG)
                        .setDisabled(!getDevice().getStatus().isOnline());
            }
            case "updating" -> uiInputBuilder.addInfo("UPDATING");
            case "idle" -> uiInputBuilder.addButton(getEntityID(), new Icon("fas fa-check-to-slot", "#72A7A1"),
                            (entityContext, params) -> sendRequest(Request.check))
                    .setText("ZIGBEE.CHECK_UPDATES")
                    .setConfirmMessage("W.CONFIRM.ZIGBEE_CHECK_UPDATES")
                    .setDisabled(!getDevice().getStatus().isOnline());
            default -> uiInputBuilder.addInfo("-");
        }
    }

    public Boolean isOutdated() {
        JsonNode node = ((JsonType) getValue()).getJsonNode();
        return node.path("state").asText().equals("available");
    }

    public Boolean isUpdating() {
        JsonNode node = ((JsonType) getValue()).getJsonNode();
        return node.path("state").asText().equals("updating");
    }

    private ActionResponseModel sendRequest(Request request) {
        getDeviceService().publish("bridge/request/device/ota_update/" + request.name(),
                new JSONObject().put("id", getDeviceService().getIeeeAddress()));
        return ActionResponseModel.showSuccess(Lang.getServerMessage("ZIGBEE.REQUEST_UPDATE_" + request.name().toUpperCase(),
                getDeviceService().getIeeeAddress()));
    }

    private enum Request {
        update, check
    }
}
