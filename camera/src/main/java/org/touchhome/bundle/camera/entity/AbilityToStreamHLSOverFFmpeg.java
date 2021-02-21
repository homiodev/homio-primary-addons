package org.touchhome.bundle.camera.entity;

import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.camera.ui.RestartHandlerOnChange;

import java.util.List;

public interface AbilityToStreamHLSOverFFmpeg<T> extends HasJsonData<T> {
    @UIField(order = 1000, onlyEdit = true, advanced = true, type = UIFieldType.Chips)
    @UIFieldGroup("hls_group")
    @RestartHandlerOnChange
    default List<String> getHlsExtraOptions() {
        return getJsonDataList("hlsOptions");
    }

    default void setHlsExtraOptions(String value) {
        setJsonData("hlsOptions", value);
    }

    @UIField(order = 320, onlyEdit = true, advanced = true)
    @UIFieldGroup("hls_group")
    @RestartHandlerOnChange
    default int getHlsListSize() {
        return getJsonData("hlsListSize", 5);
    }

    default T setHlsListSize(int value) {
        setJsonData("hlsListSize", value);
        return (T) this;
    }

    @UIField(order = 400, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    @UIFieldGroup("hls_group")
    default String getHlsVideoCodec() {
        return getJsonData("hls_vcodec", "copy");
    }

    default T setHlsVideoCodec(String value) {
        setJsonData("hls_vcodec", value);
        return (T) this;
    }

    @UIField(order = 410, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    @UIFieldGroup("hls_group")
    default String getHlsAudioCodec() {
        return getJsonData("hls_acodec", "aac");
    }

    default T setHlsAudioCodec(String value) {
        setJsonData("hls_acodec", value);
        return (T) this;
    }

    @UIField(order = 320, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    @UIFieldGroup("hls_group")
    default String getHlsScale() {
        return getJsonData("hls_scale");
    }

    default T setHlsScale(String value) {
        setJsonData("hls_scale", value);
        return (T) this;
    }
}
