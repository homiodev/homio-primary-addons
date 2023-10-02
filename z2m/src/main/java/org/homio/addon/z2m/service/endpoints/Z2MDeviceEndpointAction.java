package org.homio.addon.z2m.service.endpoints;

import lombok.val;
import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.endpoints.inline.Z2MDeviceEndpointActionEvent;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MDeviceEndpointAction extends Z2MDeviceEndpoint {

    public Z2MDeviceEndpointAction(@NotNull EntityContext entityContext) {
        super(new Icon("fa fa-circle-play", "#9636d6"), entityContext);
    }

    public static Z2MDeviceEndpointActionEvent createActionEvent(String action, Z2MDeviceService deviceService, EntityContext entityContext) {
        val configDeviceEndpoint = deviceService.getConfigDeviceEndpoint(action);
        val ActionEventEndpoint = new Z2MDeviceEndpointActionEvent(deviceService, action, configDeviceEndpoint);
        entityContext.ui().updateItem(deviceService.getDeviceEntity());
        return ActionEventEndpoint;
    }

    @Override
    public void mqttUpdate(JSONObject payload) {
        String action = payload.getString("action");
        super.mqttUpdate(payload);

        Z2MDeviceService deviceService = getDeviceService();
        String actionKey = "action_" + action;
        Z2MDeviceEndpoint endpoint = deviceService.getEndpoints().get(actionKey);
        if (endpoint == null) {
            endpoint = deviceService.addDynamicEndpoint(actionKey, () ->
                Z2MDeviceEndpointAction.createActionEvent(actionKey, deviceService, getEntityContext()));

            deviceService.addDynamicEndpoint("action_any", () ->
                Z2MDeviceEndpointAction.createActionEvent("action_any", deviceService, getEntityContext()));
        }
        endpoint.mqttUpdate(payload);
        // 'action' counter
        deviceService.getEndpoints().get("action_any").mqttUpdate(payload);
    }

    @Override
    public @NotNull String getEndpointDefinition() {
        return "action";
    }
}
