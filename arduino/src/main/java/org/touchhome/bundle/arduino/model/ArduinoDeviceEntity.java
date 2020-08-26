package org.touchhome.bundle.arduino.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.converter.JsonBeanConverter;
import org.touchhome.bundle.api.converter.bean.JsonBean;
import org.touchhome.bundle.api.model.DeviceBaseEntity;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.selection.UIFieldBeanSelection;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.arduino.provider.ArduinoCommunicationProvider;

import javax.persistence.Convert;
import javax.persistence.Entity;

@Entity
@UISidebarMenu(icon = "fas fa-tablet-alt", parent = UISidebarMenu.TopSidebarMenu.HARDWARE, order = 5, bg = "#7482d0", allowCreateNewItems = true)
@Accessors(chain = true)
public class ArduinoDeviceEntity extends DeviceBaseEntity<ArduinoDeviceEntity> {

    @UIField(readOnly = true, order = 21)
    @Getter
    @Setter
    private Long pipe;

    @Getter
    @Setter
    @UIField(order = 22)
    @JsonBean
    @Convert(converter = JsonBeanConverter.class)
    @UIFieldBeanSelection
    @UIFieldSelectValueOnEmpty(label = "selection.provider", color = "#A7D21E")
    private ArduinoCommunicationProvider communicationProvider;

    @Override
    public String getShortTitle() {
        return "Arduino";
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
