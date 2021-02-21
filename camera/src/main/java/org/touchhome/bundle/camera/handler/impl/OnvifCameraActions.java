package org.touchhome.bundle.camera.handler.impl;

import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.OnvifConnection;

public interface OnvifCameraActions {
    default int getServerPort() {
        throw new RuntimeException("Not implemented");
    }

    default void onDeviceInformationReceived(OnvifConnection.GetDeviceInformationResponse deviceInformation) {
        throw new RuntimeException("Not implemented");
    }

    default String getSnapshotUri() {
        throw new RuntimeException("Not implemented");
    }

    default void setSnapshotUri(String snapshotUri) {
        throw new RuntimeException("Not implemented");
    }

    default OnvifCameraEntity getCameraEntity() {
        throw new RuntimeException("Not implemented");
    }

    default void setRtspUri(String rtspUri) {
        throw new RuntimeException("Not implemented");
    }

    default void motionDetected(boolean on, String channel) {
        throw new RuntimeException("Not implemented");
    }

    default void audioDetected(boolean on) {
        throw new RuntimeException("Not implemented");
    }

    default void setAttribute(String key, State state) {
        throw new RuntimeException("Not implemented");
    }

    default void onCameraNameReceived(String name) {
        throw new RuntimeException("Not implemented");
    }

    void cameraUnreachable(String message);

    void cameraFaultResponse(int code, String reason);
}
