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
    protected String createRtspUri() {
        return cameraEntity.getIeeeAddress();
    }

    @Override
    public String getFFMPEGInputOptions() {
        return "";
    }

    @Override
    public void testOnline() {

    }
}
