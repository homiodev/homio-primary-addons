package org.touchhome.bundle.zigbee.setting.advanced;

import com.zsmartsystems.zigbee.transport.TrustCentreJoinMode;
import org.touchhome.bundle.api.setting.BundleSettingPluginSelectBoxEnum;

public class ZigbeeTrustCenterModeSetting implements BundleSettingPluginSelectBoxEnum<TrustCentreJoinMode> {

    @Override
    public Class<TrustCentreJoinMode> getType() {
        return TrustCentreJoinMode.class;
    }

    @Override
    public boolean allowEmpty() {
        return true;
    }

    @Override
    public int order() {
        return 1300;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
