package org.touchhome.bundle.serial.settings.header;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.setting.SettingPluginOptionsEnum;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;

public class ConsoleHeaderSerialPortSendEndLine implements ConsoleHeaderSettingPlugin<ConsoleHeaderSerialPortSendEndLine.EndLineType>,
        SettingPluginOptionsEnum<ConsoleHeaderSerialPortSendEndLine.EndLineType> {

    @Override
    public String getIcon() {
        return "fas fa-digital-tachograph";
    }

    @Override
    public Integer getMaxWidth() {
        return 135;
    }

    @Override
    public Class<EndLineType> getType() {
        return EndLineType.class;
    }

    @Override
    public boolean transientState() {
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
