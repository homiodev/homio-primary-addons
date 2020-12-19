package org.touchhome.bundle.raspberry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.PinMode;
import lombok.Getter;
import lombok.Setter;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.micro.MicroControllerBaseEntity;
import org.touchhome.bundle.api.entity.workspace.bool.WorkspaceBooleanEntity;
import org.touchhome.bundle.api.entity.workspace.bool.WorkspaceBooleanGroupEntity;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldExpand;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.method.UIFieldCreateWorkspaceVariableOnEmpty;
import org.touchhome.bundle.api.util.RaspberryGpioPin;
import org.touchhome.bundle.raspberry.RaspberryGPIOService;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public final class RaspberryDeviceEntity extends MicroControllerBaseEntity<RaspberryDeviceEntity> {

    public static final String PREFIX = "raspb_";
    public static final String DEFAULT_DEVICE_ENTITY_ID = PREFIX + "LocalRaspberry";

    @Getter
    @Setter
    @Transient
    @UIField(order = 40, type = UIFieldType.Selection, readOnly = true, color = "#7FBBCC")
    @UIFieldExpand
    @UIFieldCreateWorkspaceVariableOnEmpty
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Map<OptionModel, String>> availableLinks;

    @Override
    public String getShortTitle() {
        return "Rpi";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void afterFetch(EntityContext entityContext) {
        super.afterFetch(entityContext);
        gatherAvailableLinks(entityContext);
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }

    private void gatherAvailableLinks(EntityContext entityContext) {
        List<Map<OptionModel, String>> links = new ArrayList<>();
        RaspberryGPIOService raspberryGPIOService = entityContext.getBean(RaspberryGPIOService.class);
        for (RaspberryGpioPin gpioPin : RaspberryGpioPin.values(PinMode.DIGITAL_INPUT, null)) {
            Map<OptionModel, String> map = new HashMap<>();
            map.put(OptionModel.of(gpioPin.name(), gpioPin.toString()).json(json ->
                            json.put("group", WorkspaceBooleanGroupEntity.PREFIX).put("color", gpioPin.getColor())
                                    .put("var", WorkspaceBooleanEntity.PREFIX)),
                    getLinkedWorkspaceBooleanVariable(gpioPin, entityContext, raspberryGPIOService));
            links.add(map);
        }
        setAvailableLinks(links);
    }

    private String getLinkedWorkspaceBooleanVariable(RaspberryGpioPin gpioPin, EntityContext entityContext,
                                                     RaspberryGPIOService raspberryGPIOService) {
        List<RaspberryGPIOService.PinListener> pinListeners = raspberryGPIOService.getDigitalListeners().get(gpioPin);
        if (pinListeners != null) {
            for (RaspberryGPIOService.PinListener pinListener : pinListeners) {
                if (pinListener.getName().startsWith(WorkspaceBooleanEntity.PREFIX)) {
                    WorkspaceBooleanEntity variableEntity = entityContext.getEntity(pinListener.getName());
                    if (variableEntity != null) {
                        return variableEntity.getTitle();
                    }
                }
            }
        }
        return "";
    }
}
