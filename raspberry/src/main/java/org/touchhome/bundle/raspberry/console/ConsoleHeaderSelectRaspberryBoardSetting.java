package org.touchhome.bundle.raspberry.console;

import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginOptions;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity;

import java.util.Collection;

public class ConsoleHeaderSelectRaspberryBoardSetting implements ConsoleHeaderSettingPlugin<String>,
        SettingPluginOptions<String> {

    @Override
    public Collection<OptionModel> getOptions(EntityContext entityContext, JSONObject params) {
        return OptionModel.list(entityContext.findAll(RaspberryDeviceEntity.class));
    }

    @Override
    public String getDefaultValue() {
        return RaspberryDeviceEntity.DEFAULT_DEVICE_ENTITY_ID;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public Integer getMaxWidth() {
        return 150;
    }

    @Override
    public String getIcon() {
        return "fab fa-raspberry-pi";
    }

    @Override
    public UIFieldType getSettingType() {
        return UIFieldType.SelectBoxDynamic;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
