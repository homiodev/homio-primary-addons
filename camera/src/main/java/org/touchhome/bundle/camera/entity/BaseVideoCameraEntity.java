package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.exception.ServerException;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.netty.HasBootstrapServer;
import org.touchhome.bundle.api.netty.NettyUtils;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldCodeEditor;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.action.UIActionButton;
import org.touchhome.bundle.api.ui.field.action.UIActionInput;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.action.impl.StatefulContextMenuAction;
import org.touchhome.bundle.api.ui.field.image.UIFieldImage;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.handler.BaseCameraHandler;
import org.touchhome.bundle.camera.ui.RestartHandlerOnChange;

import javax.persistence.Transient;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Log4j2
@Setter
@Getter
public abstract class BaseVideoCameraEntity<T extends BaseVideoCameraEntity, H extends BaseCameraHandler>
        extends BaseVideoStreamEntity<T> {

    @Transient
    @JsonIgnore
    private H cameraHandler;

    @Override
    @UIFieldIgnore
    public Status getJoined() {
        return super.getJoined();
    }

    @UIField(order = 15, inlineEdit = true)
    public boolean isStart() {
        return getJsonData("start", false);
    }

    public BaseVideoCameraEntity<T, H> setStart(boolean start) {
        setJsonData("start", start);
        return this;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @UIField(order = 500, readOnly = true)
    @UIFieldImage
    @UIActionButton(name = "refresh", icon = "fas fa-sync", actionHandler = BaseVideoCameraEntity.UpdateSnapshotActionHandler.class)
    public byte[] getLastSnapshot() {
        return cameraHandler == null ? null : cameraHandler.getLatestSnapshot();
    }

    @UIField(order = 250, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    public int getServerPort() {
        return getJsonData("serverPort", -1);
    }

    public void setServerPort(int value) {
        setJsonData("serverPort", value);
    }

    @UIField(order = 300, onlyEdit = true, advanced = true)
    public boolean isHasAudioStream() {
        return getJsonData("hasAudioStream", false);
    }

    public T setHasAudioStream(boolean value) {
        setJsonData("hasAudioStream", value);
        return (T) this;
    }

    @Override
    protected void fireUpdateSnapshot(EntityContext entityContext, JSONObject params) {
        if (!isStart()) {
            throw new ServerException("Camera <" + getTitle() + "> not started");
        }
        getCameraHandler().startSnapshot();
    }

    @UIField(order = 200, readOnly = true)
    @UIFieldCodeEditor(editorType = UIFieldCodeEditor.CodeEditorType.json, autoFormat = true)
    public Map<String, State> getAttributes() {
        return getCameraHandler() == null ? null : getCameraHandler().getAttributes();
    }

    public abstract H createCameraHandler(EntityContext entityContext);

    @Override
    public Set<StatefulContextMenuAction> getActions(boolean fetchValues) {
        return cameraHandler.getCameraActions(fetchValues);
    }

    @UIContextMenuAction(value = "RECORD_MP4", icon = "fas fa-file-video", inputs = {
            @UIActionInput(name = "fileName", value = "record_${timestamp}", min = 4, max = 30),
            @UIActionInput(name = "secondsToRecord", type = UIActionInput.Type.number, value = "10", min = 5, max = 100)
    })
    public ActionResponseModel recordMP4(JSONObject params) {
        String filename = getFileNameToRecord(params);
        int secondsToRecord = params.getInt("secondsToRecord");
        log.debug("Recording {}.mp4 for {} seconds.", filename, secondsToRecord);
        this.cameraHandler.recordMp4(filename, secondsToRecord);
        return null;
    }

    @UIContextMenuAction(value = "RECORD_GIF", icon = "fas fa-magic", inputs = {
            @UIActionInput(name = "fileName", value = "record_${timestamp}", min = 4, max = 30),
            @UIActionInput(name = "secondsToRecord", type = UIActionInput.Type.number, value = "3", min = 1, max = 10)
    })
    public ActionResponseModel recordGif(JSONObject params) {
        String filename = getFileNameToRecord(params);
        int secondsToRecord = params.getInt("secondsToRecord");
        log.debug("Recording {}.gif for {} seconds.", filename, secondsToRecord);
        this.cameraHandler.recordGif(filename, secondsToRecord);
        return null;
    }

    private String getFileNameToRecord(JSONObject params) {
        String fileName = params.getString("fileName");
        // hacky
        fileName = fileName.replace("${timestamp}", System.currentTimeMillis() + "");
        return fileName;
    }

    @Override
    protected void beforePersist() {
        super.beforePersist();
        this.setStatus(Status.UNKNOWN);
    }

    @Override
    public void afterFetch(EntityContext entityContext) {
        H cameraHandler = (H) NettyUtils.putBootstrapServer(getEntityID(), (Supplier<HasBootstrapServer>) () -> createCameraHandler(entityContext));
        setCameraHandler(cameraHandler);
        setHlsStreamUrl("http://" + TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + cameraHandler.getServerPort() + "/ipcamera.m3u8");
    }
}