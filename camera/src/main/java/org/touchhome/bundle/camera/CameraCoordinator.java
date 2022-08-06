package org.touchhome.bundle.camera;

import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.camera.handler.BaseBrandCameraHandler;
import org.touchhome.bundle.camera.onvif.brand.CameraBrandHandlerDescription;
import org.touchhome.bundle.camera.rtsp.message.sdp.SdpMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CameraCoordinator {

    public static Map<String, BaseBrandCameraHandler> entityToCameraBrands = new ConcurrentHashMap<>();

    public static Map<String, CameraBrandHandlerDescription> cameraBrands = new ConcurrentHashMap<>();

    private static Map<String, SdpMessage> rtspUrlToSdpMessage = new ConcurrentHashMap<>();

    public static SdpMessage getSdpMessage(String key) {
        return key == null ? null : rtspUrlToSdpMessage.get(key);
    }

    public static void setSdpMessage(String key, SdpMessage sdpMessage) {
        rtspUrlToSdpMessage.put(key, sdpMessage);
    }

    public static void removeSpdMessage(String key) {
        if (StringUtils.isNotEmpty(key)) {
            rtspUrlToSdpMessage.remove(key);
        }
    }
}
