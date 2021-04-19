package org.touchhome.bundle.camera.ui;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;

public interface CameraActionsContext {
    State getAttribute(String key);

    BaseVideoCameraEntity getCameraEntity();

    EntityContext getEntityContext();
}
