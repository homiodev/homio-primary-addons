package org.touchhome.bundle.camera.onvif.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.touchhome.bundle.camera.onvif.brand.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.service.OnvifCameraService;

/**
 * responsible for handling commands for generic onvif thing types.
 */
@CameraBrandHandler(name = "Onvif")
public class OnvifBrandHandler extends BaseOnvifCameraBrandHandler {

  public OnvifBrandHandler(OnvifCameraService service) {
    super(service);
  }

  @Override
  public boolean isSupportOnvifEvents() {
    return true;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ReferenceCountUtil.release(msg);
  }
}
