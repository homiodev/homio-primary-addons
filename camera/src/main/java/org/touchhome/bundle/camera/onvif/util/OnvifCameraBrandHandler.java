package org.touchhome.bundle.camera.onvif.util;

import io.netty.channel.ChannelDuplexHandler;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.action.impl.StatefulContextMenuAction;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.ui.CameraActionBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public abstract class OnvifCameraBrandHandler extends ChannelDuplexHandler {
    private static Map<String, List<StatefulContextMenuAction>> actions = new HashMap<>();

    protected final OnvifCameraHandler onvifCameraHandler;
    protected final int nvrChannel;

    protected final String username;
    protected final String password;

    public OnvifCameraBrandHandler(OnvifCameraHandler onvifCameraHandler) {
        this.onvifCameraHandler = onvifCameraHandler;
        this.nvrChannel = 0;
        this.username = "";
        this.password = "";
    }

    public OnvifCameraBrandHandler(OnvifCameraEntity onvifCameraEntity) {
        this.onvifCameraHandler = onvifCameraEntity.getCameraHandler();
        this.nvrChannel = onvifCameraEntity.getNvrChannel();
        this.username = onvifCameraEntity.getUser();
        this.password = onvifCameraEntity.getPassword();
    }

    protected State getState(String name) {
        return onvifCameraHandler.getAttributes().getOrDefault(name, null);
    }

    public int boolToInt(boolean on) {
        return on ? 1 : 0;
    }

    public List<StatefulContextMenuAction> getCameraActions() {
        return actions.computeIfAbsent(getClass().getSimpleName(), key -> CameraActionBuilder.assemble(this, this));
    }

    protected void setAttribute(String key, State state) {
        onvifCameraHandler.setAttribute(key, state);
    }

    public abstract List<String> getLowPriorityRequests();
}
