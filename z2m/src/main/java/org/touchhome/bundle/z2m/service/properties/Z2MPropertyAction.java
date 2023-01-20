package org.touchhome.bundle.z2m.service.properties;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.z2m.service.Z2MDeviceService;
import org.touchhome.bundle.z2m.service.Z2MProperty;
import org.touchhome.bundle.z2m.service.properties.dynamic.Z2MPropertyActionEvent;
import org.touchhome.bundle.z2m.util.Z2MPropertyDTO;

public class Z2MPropertyAction extends Z2MProperty {

    public Z2MPropertyAction() {
        super("#9636d6", "fa fa-fw fa-circle-play");
    }

    @Override
    public void mqttUpdate(JSONObject payload) {
        String action = payload.getString("action");

        setUpdated(System.currentTimeMillis());
        setValue(new StringType(action));
        updateUI();

        Z2MDeviceService deviceService = getDeviceService();
        EntityContext entityContext = deviceService.getCoordinatorService().getEntityContext();
        deviceService.getProperties().computeIfAbsent(action, s -> createActionEvent(action, deviceService, entityContext));
        Z2MPropertyActionEvent z2MProperty = (Z2MPropertyActionEvent) deviceService.getProperties().get(action);
        z2MProperty.mqttUpdate(payload);
    }

    @NotNull
    private Z2MPropertyActionEvent createActionEvent(String action, Z2MDeviceService deviceService, EntityContext entityContext) {
        Z2MPropertyDTO z2MPropertyDTO = deviceService.getCoordinatorService().getZ2mProperties().get(action);
        Z2MPropertyActionEvent z2MPropertyActionEvent = new Z2MPropertyActionEvent(deviceService, action, z2MPropertyDTO);
        entityContext.ui().updateItem(deviceService.getDeviceEntity());
        return z2MPropertyActionEvent;
    }

    @Override
    public String getPropertyDefinition() {
        return "action";
    }
}
