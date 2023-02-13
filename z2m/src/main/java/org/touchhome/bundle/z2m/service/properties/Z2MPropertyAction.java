package org.touchhome.bundle.z2m.service.properties;

import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.z2m.service.Z2MDeviceService;
import org.touchhome.bundle.z2m.service.Z2MProperty;
import org.touchhome.bundle.z2m.service.properties.dynamic.Z2MPropertyActionEvent;
import org.touchhome.bundle.z2m.util.Z2MDevicePropertiesDTO;
import org.touchhome.bundle.z2m.util.ZigBeeUtil;

public class Z2MPropertyAction extends Z2MProperty {

    public Z2MPropertyAction() {
        super("#9636d6", "fa fa-fw fa-circle-play");
    }

    public static Z2MPropertyActionEvent createActionEvent(String action, Z2MDeviceService deviceService, EntityContext entityContext) {
        Z2MDevicePropertiesDTO z2MDevicePropertiesDTO = ZigBeeUtil.DEVICE_PROPERTIES.get(action);
        Z2MPropertyActionEvent z2MPropertyActionEvent = new Z2MPropertyActionEvent(deviceService, action, z2MDevicePropertiesDTO);
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
    public String getPropertyDefinition() {
        return "action";
    }
}
