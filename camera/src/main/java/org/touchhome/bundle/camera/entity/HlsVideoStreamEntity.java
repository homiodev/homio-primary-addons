package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.camera.handler.impl.HlsStreamHandler;

import javax.persistence.Entity;

@Setter
@Getter
@Entity
@Accessors(chain = true)
public class HlsVideoStreamEntity extends BaseFFmpegStreamEntity<HlsVideoStreamEntity, HlsStreamHandler> {

    public static final String PREFIX = "hls_";

    @Override
    @JsonIgnore
    @UIFieldIgnore
    public boolean isStart() {
        return super.isStart();
    }

    @Override
    @UIField(order = 5, label = "hlsUrl")
    @RestartHandlerOnChange
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    @Override
    public String toString() {
        return "hls:" + getIeeeAddress();
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }

    @Override
    public HlsStreamHandler createCameraHandler(EntityContext entityContext) {
        return new HlsStreamHandler(this, entityContext);
    }

    @Override
    public String getHlsStreamUrl() {
        return getIeeeAddress();
    }

    @Override
    public void afterFetch(EntityContext entityContext) {
        super.afterFetch(entityContext);
        setStart(true);
    }

    @Override
    protected void beforePersist() {
        setSnapshotOutOptions("-update 1~~~-frames:v 1");
    }
}
