package org.touchhome.bundle.camera.onvif.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;

/**
 * responsible for handling commands for generic onvif thing types.
 */
@CameraBrandHandler(name = "Onvif")
public class OnvifBrandHandler extends BaseOnvifCameraBrandHandler {

    public OnvifBrandHandler(OnvifCameraEntity onvifCameraEntity) {
        super(onvifCameraEntity);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }
}
