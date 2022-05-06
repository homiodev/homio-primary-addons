package org.touchhome.bundle.camera.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface BaseBrandCameraHandler {
    boolean isSupportOnvifEvents();

    void handleSetURL(ChannelPipeline pipeline, String httpRequestURL);

    void assembleActions(UIInputBuilder uiInputBuilder);

    default ChannelHandler asBootstrapHandler() {
        throw new RuntimeException("Unsupported bootstrap handler");
    }

    void pollCameraRunnable(OnvifCameraHandler onvifCameraHandler);

    void initialize(EntityContext entityContext);

    void runOncePerMinute(EntityContext entityContext);

    String getUrlToKeepOpenForIdleStateEvent();

    default Consumer<Boolean> getIRLedHandler() {
        return null;
    }

    default Supplier<Boolean> getIrLedValueHandler() {
        return null;
    }
}
