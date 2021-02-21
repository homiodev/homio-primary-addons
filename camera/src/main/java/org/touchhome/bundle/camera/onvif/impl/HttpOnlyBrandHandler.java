package org.touchhome.bundle.camera.onvif.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.util.OnvifCameraBrandHandler;

import java.util.ArrayList;

/**
 * responsible for handling commands for generic onvif thing types.
 */
public class HttpOnlyBrandHandler extends OnvifCameraBrandHandler {

    public HttpOnlyBrandHandler(OnvifCameraEntity onvifCameraEntity) {
        super(onvifCameraEntity);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }

    public ArrayList<String> getLowPriorityRequests() {
        return null;
    }
}
