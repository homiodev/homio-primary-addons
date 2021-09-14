package org.touchhome.bundle.zigbee.setting;

import com.fazecast.jSerialComm.SerialPort;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.setting.SettingPluginStatus;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;

public class ZigBeeStatusSetting implements SettingPluginStatus {

    @Override
    public int order() {
        return 400;
    }

    @Override
    public void setActions(UIInputBuilder actionSupplier) {
        actionSupplier.addSelectBox("port", (entityContext, params) -> {
            entityContext.setting().setValue(ZigBeePortSetting.class, SerialPort.getCommPort(params.getString("value")));
            BundleStatusInfo value = entityContext.setting().getValue(ZigBeeStatusSetting.class);
            if (value.getStatus() == Status.ONLINE) {
                return ActionResponseModel.showSuccess("Success");
            }
            return ActionResponseModel.showError(value.getMessage());
        }).setAsButton("fas fa-random", "primary", "SELECT_PORT").setOptions(OptionModel.listOfPorts(true));
    }
}
