package org.touchhome.bundle.serial.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.port.PortFlowControl;
import org.touchhome.bundle.api.setting.BundleSettingPluginSelectBoxEnum;

public class SerialPortFlowControlSetting implements BundleSettingPluginSelectBoxEnum<SerialPortFlowControlSetting.FlowControl> {

    @Override
    public String getIcon() {
        return "fas fa-wind";
    }

    @Override
    public Integer getMaxWidth() {
        return 110;
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public Class<FlowControl> getType() {
        return FlowControl.class;
    }

    @Override
    public boolean transientState() {
        return false;
    }

    @RequiredArgsConstructor
    public enum FlowControl {
        NONE(PortFlowControl.FLOWCONTROL_OUT_NONE),
        XONOFF(PortFlowControl.FLOWCONTROL_OUT_XONOFF),
        RTSCTS(PortFlowControl.FLOWCONTROL_OUT_RTSCTS);

        @Getter
        private final PortFlowControl portFlowControl;
    }
}
