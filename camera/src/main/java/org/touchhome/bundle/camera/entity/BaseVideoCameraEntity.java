package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.exception.ServerException;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.netty.HasBootstrapServer;
import org.touchhome.bundle.api.netty.NettyUtils;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.*;
import org.touchhome.bundle.api.ui.field.action.UIActionButton;
import org.touchhome.bundle.api.ui.field.action.UIActionInput;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.image.UIFieldImage;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.handler.BaseCameraHandler;

import javax.persistence.Transient;
import java.nio.file.Path;
import java.util.Map;
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

    @UIField(order = 16, onlyEdit = true)
    public boolean isAutoStart() {
        return getJsonData("autoStart", false);
    }

    public BaseVideoCameraEntity<T, H> setAutoStart(boolean start) {
        setJsonData("autoStart", start);
        return this;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @UIField(order = 500, readOnly = true)
    @UIFieldImage
    @UIActionButton(name = "refresh", icon = "fas fa-sync", actionHandler = BaseVideoCameraEntity.UpdateSnapshotActionHandler.class)
    @UIFieldIgnoreGetDefault
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

    @UIField(order = 500, readOnly = true, type = UIFieldType.Duration)
    public long getLastAnswerFromCamera() {
        return cameraHandler == null ? 0 : cameraHandler.getLastAnswerFromCamera();
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
        if (cameraHandler != null) {
            cameraHandler.startSnapshot();
        }
    }

    @UIField(order = 200, readOnly = true)
    @UIFieldCodeEditor(editorType = UIFieldCodeEditor.CodeEditorType.json, autoFormat = true)
    public Map<String, State> getAttributes() {
        return cameraHandler == null ? null : cameraHandler.getAttributes();
    }

    public abstract H createCameraHandler(EntityContext entityContext);


    @Override
    public UIInputBuilder assembleActions() {
        return cameraHandler == null ? null : cameraHandler.assembleActions();
    }

    @UIContextMenuAction(value = "VIDEO.RECORD_MP4", icon = "fas fa-file-video", inputs = {
            @UIActionInput(name = "fileName", value = "record_${timestamp}", min = 4, max = 30),
            @UIActionInput(name = "secondsToRecord", type = UIActionInput.Type.number, value = "10", min = 5, max = 100)
    })
    public ActionResponseModel recordMP4(JSONObject params) {
        if (cameraHandler == null) {
            return ActionResponseModel.showError("Camera handler is empty");
        }
        String filename = getFileNameToRecord(params);
        int secondsToRecord = params.getInt("secondsToRecord");
        log.debug("Recording {}.mp4 for {} seconds.", filename, secondsToRecord);
        cameraHandler.recordMp4(filename, null, secondsToRecord);
        return ActionResponseModel.showSuccess("SUCCESS");
    }

    @UIContextMenuAction(value = "VIDEO.RECORD_GIF", icon = "fas fa-magic", inputs = {
            @UIActionInput(name = "fileName", value = "record_${timestamp}", min = 4, max = 30),
            @UIActionInput(name = "secondsToRecord", type = UIActionInput.Type.number, value = "3", min = 1, max = 10)
    })
    public ActionResponseModel recordGif(JSONObject params) {
        if (cameraHandler == null) {
            return ActionResponseModel.showError("Camera handler is empty");
        }
        String filename = getFileNameToRecord(params);
        int secondsToRecord = params.getInt("secondsToRecord");
        log.debug("Recording {}.gif for {} seconds.", filename, secondsToRecord);
        cameraHandler.recordGif(filename, null, secondsToRecord);
        return ActionResponseModel.showSuccess("SUCCESS");
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
    }

    @Override
    public void afterFetch(EntityContext entityContext) {
        H cameraHandler = (H) NettyUtils.putBootstrapServer(getEntityID(), (Supplier<HasBootstrapServer>) () -> createCameraHandler(entityContext));
        setCameraHandler(cameraHandler);

        if (getStatus() == Status.UNKNOWN) {
            try {
                getCameraHandler().testOnline();
                setStatusOnline();
            } catch (BadCredentialsException ex) {
                setStatus(Status.REQUIRE_AUTH, ex.getMessage());
            } catch (Exception ex) {
                setStatusError(ex);
            }
        }
    }

    @Override
    public void afterUpdate(EntityContext entityContext) {
        setStatus(Status.UNKNOWN);
    }

    public Path getFolder() {
        return TouchHomeUtils.getMediaPath().resolve("camera").resolve(getEntityID());
    }

    public Path getFolder(String profile) {
        return TouchHomeUtils.getMediaPath().resolve("camera").resolve(getEntityID()).resolve(profile);
    }
}
