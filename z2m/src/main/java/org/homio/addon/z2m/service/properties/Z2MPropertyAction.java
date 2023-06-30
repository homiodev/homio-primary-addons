package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.service.properties.inline.Z2MPropertyActionEvent;
import org.homio.addon.z2m.util.Z2MPropertyModel;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.homio.api.state.StringType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MPropertyAction extends Z2MProperty {

    public Z2MPropertyAction() {
        super(new Icon( "fa fa-fw fa-circle-play", "#9636d6"));
    }

    public static Z2MPropertyActionEvent createActionEvent(String action, Z2MDeviceService deviceService, EntityContext entityContext) {
        Z2MPropertyModel z2MPropertyModel = deviceService.getPropertyModel(action);
        Z2MPropertyActionEvent z2MPropertyActionEvent = new Z2MPropertyActionEvent(deviceService, action, z2MPropertyModel);
        entityContext.ui().updateItem(deviceService.getDeviceEntity());
        return z2MPropertyActionEvent;
    }

    @Override
    public void mqttUpdate(JSONObject payload) {
        String action = payload.getString("action");

        setUpdated(System.currentTimeMillis());
        setValue(new StringType(action));
        updateUI();

        Z2MDeviceService deviceService = getDeviceService();
        String actionKey = "action_" + action;
        Z2MProperty z2MProperty = deviceService.getProperties().get(actionKey);
        if (z2MProperty == null) {
            EntityContext entityContext = deviceService.getCoordinatorService().getEntityContext();
            z2MProperty = deviceService.addDynamicProperty(actionKey, () ->
                Z2MPropertyAction.createActionEvent(actionKey, deviceService, entityContext));

            deviceService.addDynamicProperty("action_any", () ->
                Z2MPropertyAction.createActionEvent("action_any", deviceService, entityContext));
        }
        z2MProperty.mqttUpdate(payload);
        deviceService.getProperties().get("action_any").mqttUpdate(payload);
    }

    @Override
    public @NotNull String getPropertyDefinition() {
        return "action";
    }
}
