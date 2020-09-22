package org.touchhome.bundle.zigbee.setting.advanced;

import com.zsmartsystems.zigbee.transport.TrustCentreJoinMode;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.setting.BundleSettingPlugin;

import java.util.List;

public class ZigbeeTrustCenterModeSetting implements BundleSettingPlugin<TrustCentreJoinMode> {

    @Override
    public SettingType getSettingType() {
        return SettingType.SelectBox;
    }

    @Override
    public TrustCentreJoinMode parseValue(EntityContext entityContext, String value) {
        return StringUtils.isEmpty(value) ? null : TrustCentreJoinMode.valueOf(value);
    }

    @Override
    public List<Option> loadAvailableValues(EntityContext entityContext) {
        return Option.enumWithEmpty(TrustCentreJoinMode.class);
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
