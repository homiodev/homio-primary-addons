package org.homio.addon.z2m.service.endpoints;

import lombok.val;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.Z2MEndpoint;
import org.homio.addon.z2m.service.endpoints.inline.Z2MEndpointActionEvent;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MEndpointAction extends Z2MEndpoint {

    public Z2MEndpointAction() {
        super(new Icon( "fa fa-fw fa-circle-play", "#9636d6"));
    }

    public static Z2MEndpointActionEvent createActionEvent(String action, Z2MDeviceService deviceService, EntityContext entityContext) {
        val configDeviceEndpoint = deviceService.getConfigDeviceEndpoint(action);
        val ActionEventEndpoint = new Z2MEndpointActionEvent(deviceService, action, configDeviceEndpoint);
        entityContext.ui().updateItem(deviceService.getDeviceEntity());
        return ActionEventEndpoint;
    }

    @Override
    public void mqttUpdate(JSONObject payload) {
        String action = payload.getString("action");
        super.mqttUpdate(payload);

        Z2MDeviceService deviceService = getDeviceService();
        String actionKey = "action_" + action;
        Z2MEndpoint z2MEndpoint = deviceService.getEndpoints().get(actionKey);
        if (z2MEndpoint == null) {
            EntityContext entityContext = deviceService.getEntityContext();
            z2MEndpoint = deviceService.addDynamicEndpoint(actionKey, () ->
                    Z2MEndpointAction.createActionEvent(actionKey, deviceService, entityContext));

            deviceService.addDynamicEndpoint("action_any", () ->
                    Z2MEndpointAction.createActionEvent("action_any", deviceService, entityContext));
        }
        z2MEndpoint.mqttUpdate(payload);
        // 'action' counter
        deviceService.getEndpoints().get("action_any").mqttUpdate(payload);
    }

    @Override
    public @NotNull String getEndpointDefinition() {
        return "action";
    }
}
