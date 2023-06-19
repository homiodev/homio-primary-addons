package org.homio.addon.z2m.service.properties;

import com.fasterxml.jackson.databind.JsonNode;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.Icon;
import org.homio.api.state.JsonType;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.util.Lang;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MPropertyFirmwareUpdate extends Z2MProperty {

    public Z2MPropertyFirmwareUpdate() {
        super(new Icon("fa fa-fw fa-tablets", "#A3102B"));
        setValue(new JsonType("{}"));
    }

    @Override
    public void init(@NotNull Z2MDeviceService deviceService, @NotNull ApplianceModel.Z2MDeviceDefinition.Options expose) {
        expose.setType(ApplianceModel.COMPOSITE_TYPE);
        super.init(deviceService, expose);
    }

    @Override
    public void fireAction(boolean value) {
        getDeviceService().getCoordinatorService().publish("bridge/request/device/ota_update/update",
            new JSONObject().put("id", getDeviceService().getIeeeAddress()));
    }

    @Override
    public String getPropertyDefinition() {
        return "update";
    }

    @Override
    public void buildZigbeeAction(UIInputBuilder uiInputBuilder, String entityID) {
        JsonNode node = ((JsonType) getValue()).getJsonNode();
        String updateTitle = node.get("installed_version").asText() + "=>" + node.get("latest_version").asText();
        uiInputBuilder.addSelectableButton("upd", new Icon("fas fa-retweet", "#A3102B"), (entityContext, params) -> {
                          fireAction(true);
                          return ActionResponseModel.fired();
                      }).setText(Lang.getServerMessage("Z2M.UPDATE", updateTitle))
                      .setConfirmMessage("W.CONFIRM.Z2M_UPDATE")
                      .setConfirmMessageDialogColor(Color.ERROR_DIALOG);
    }

    @Override
    protected void getOrCreateVariable() {
        // ignore
    }
}
