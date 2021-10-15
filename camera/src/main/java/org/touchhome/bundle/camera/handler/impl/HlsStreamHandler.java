package org.touchhome.bundle.camera.handler.impl;

import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.camera.entity.HlsVideoStreamEntity;

@Log4j2
public class HlsStreamHandler extends RtspStreamHandler {

    public HlsStreamHandler(HlsVideoStreamEntity cameraEntity, EntityContext entityContext) {
        super(cameraEntity, entityContext);
    }

    @Override
    public String getRtspUri(String profile) {
        return cameraEntity.getIeeeAddress();
    }

    @Override
    public String getFFMPEGInputOptions(String profile) {
        return "";
    }

    @Override
    public void testOnline() {

    }
}
