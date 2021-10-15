package org.touchhome.bundle.camera.setting;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.setting.SettingPluginStatus;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;
import org.touchhome.bundle.camera.entity.BaseVideoStreamEntity;

import java.util.ArrayList;
import java.util.List;

public class CameraStatusSetting implements SettingPluginStatus {

    @Override
    public int order() {
        return 400;
    }

    @Override
    public List<BundleStatusInfo> getTransientStatuses(EntityContext entityContext) {
        List<BundleStatusInfo> list = new ArrayList<>();
        for (BaseVideoStreamEntity baseVideoStreamEntity : entityContext.findAll(BaseVideoStreamEntity.class)) {
            if (baseVideoStreamEntity.getStatus() != Status.ONLINE) {
                list.add(SettingPluginStatus.error(baseVideoStreamEntity.getStatus() + " " + baseVideoStreamEntity.getTitle() + " " + baseVideoStreamEntity.getStatusMessage()));
            } else if (baseVideoStreamEntity instanceof BaseVideoCameraEntity && !((BaseVideoCameraEntity) baseVideoStreamEntity).isStart()) {
                list.add(SettingPluginStatus.error(baseVideoStreamEntity.getStatus() + " " + baseVideoStreamEntity.getTitle() + " not started")
                        .setActionHandler(uiInputBuilder ->
                                uiInputBuilder.addButton("Start", "fas fa-hammer", "primary", (entityContext1, params) -> {
                                    ((BaseVideoCameraEntity) baseVideoStreamEntity).setStart(true);
                                    entityContext1.save(baseVideoStreamEntity);
                                    return ActionResponseModel.success();
                                })));
            }
        }
        return list;
    }
}