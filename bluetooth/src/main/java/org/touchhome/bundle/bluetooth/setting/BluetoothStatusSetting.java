package org.touchhome.bundle.bluetooth.setting;

import lombok.SneakyThrows;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.setting.SettingPluginStatus;
import org.touchhome.common.util.CommonUtils;

public class BluetoothStatusSetting implements SettingPluginStatus {

    @Override
    public int order() {
        return 0;
    }

    @SneakyThrows
    @Override
    public String getDefaultValue() {
        return CommonUtils.OBJECT_MAPPER.writeValueAsString(new BundleStatusInfo(Status.UNKNOWN, ""));
    }
}
