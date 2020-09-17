package org.touchhome.bundle.arduino.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.json.JSONObject;
import org.touchhome.bundle.api.DynamicOptionLoader;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.converter.JsonBeanConverter;
import org.touchhome.bundle.api.converter.bean.JsonBean;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.model.DeviceBaseEntity;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldName;
import org.touchhome.bundle.api.ui.field.selection.UIFieldBeanSelection;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.ui.method.UIMethodAction;
import org.touchhome.bundle.arduino.provider.command.ArduinoRegisterCommand;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommunicationProvider;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@UISidebarMenu(icon = "fas fa-tablet-alt", parent = UISidebarMenu.TopSidebarMenu.HARDWARE, order = 5, bg = "#7482d0", allowCreateNewItems = true)
@Accessors(chain = true)
@NamedQueries({
        @NamedQuery(name = "ArduinoDeviceEntity.resetStatuses", query = "UPDATE ArduinoDeviceEntity set status = 'UNKNOWN', liveStatus = 'UNKNOWN'")
})
public class ArduinoDeviceEntity extends DeviceBaseEntity<ArduinoDeviceEntity> {

    public static final String PREFIX = "ad_";
    public static final int DEVICE_TYPE = 17;

    @Getter
    @Setter
    @UIField(order = 22)
    @JsonBean
    @Convert(converter = JsonBeanConverter.class)
    @UIFieldBeanSelection
    @UIFieldSelectValueOnEmpty(label = "selection.provider", color = "#A7D21E")
    private ArduinoCommunicationProvider communicationProvider;

    @Getter
    @Setter
    @UIField(order = 22, readOnly = true)
    @Enumerated(EnumType.STRING)
    private Status liveStatus = Status.UNKNOWN;

    @Override
    @UIField(order = 100, inlineEdit = true, required = true)
    @UIFieldSelection(SelectTargetArduinoDeviceLoader.class)
    @UIFieldSelectValueOnEmpty(label = "selection.target", color = "#A7D21E")
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    @UIField(order = 120, readOnly = true)
    public Integer getMissedPings() {
        return getJsonData().optInt("ping", 0);
    }

    @Override
    public JSONObject getJsonData() {
        return super.getJsonData();
    }

    @Override
    @UIFieldName("communicatorStatus")
    public Status getStatus() {
        return super.getStatus();
    }

    @UIMethodAction(name = "ACTION.COMMUNICATOR.RESTART")
    public String restartCommunicator() {
        if (communicationProvider != null) {
            if (communicationProvider.restart(this)) {
                return "ACTION.COMMUNICATOR.SUCCESS";
            }
            return "ACTION.COMMUNICATOR.FAILED";
        }
        return "ACTION.COMMUNICATOR.COMMUNICATOR_NOT_SER";
    }

    @JsonIgnore
    public short getTarget() {
        return getIeeeAddress() == null ? -1 : Short.parseShort(getIeeeAddress());
    }

    public void setTarget(short target) {
        setIeeeAddress(Short.toString(target));
    }

    @Override
    public String getShortTitle() {
        return "Arduino";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    public void setMissedPings(int missedPings) {
        getJsonData().put("ping", missedPings);
    }

    public static class SelectTargetArduinoDeviceLoader implements DynamicOptionLoader {

        @Override
        public List<Option> loadOptions(Object parameter, EntityContext entityContext) {
            return entityContext.getBean(ArduinoRegisterCommand.class).getPendingRegistrations().keySet().stream()
                    .map(arduinoMessage -> Option.key(Short.toString(arduinoMessage))).collect(Collectors.toList());
        }
    }
}
