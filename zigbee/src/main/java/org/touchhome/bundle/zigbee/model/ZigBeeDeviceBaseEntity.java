package org.touchhome.bundle.zigbee.model;

import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.ui.UISidebarMenu;

@Log4j2
@UISidebarMenu(icon = "fas fa-bezier-curve", parent = UISidebarMenu.TopSidebarMenu.HARDWARE,
               bg = "#de9ed7", order = 5, overridePath = "zigbee")
public abstract class ZigBeeDeviceBaseEntity<T extends ZigBeeDeviceBaseEntity> extends DeviceBaseEntity<T> {

}
