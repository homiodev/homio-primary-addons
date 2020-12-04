package org.touchhome.bundle.serial.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.setting.BundleSettingPluginSelectBoxEnum;

public class SerialPortSendEndLine implements BundleSettingPluginSelectBoxEnum<SerialPortSendEndLine.EndLineType> {

    @Override
    public String getIcon() {
        return "fas fa-digital-tachograph";
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public Class<EndLineType> getType() {
        return EndLineType.class;
    }

    @Override
    public boolean transientState() {
        return true;
    }

    @Override
    public boolean isVisible(EntityContext entityContext) {
        return false;
    }

    @RequiredArgsConstructor
    public enum EndLineType {
        NO_LINE_ENDING(""),
        NEW_LINE("\n"),
        CARRIAGE_RETURN("\r"),
        BOTH_NL_CR("\r\n");

        @Getter
        private final String value;
    }
}
