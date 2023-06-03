package org.homio.addon.camera.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.homio.api.EntityContext;
import org.homio.api.entity.RestartHandlerOnChange;
import org.homio.api.model.Icon;
import org.homio.api.ui.field.UIField;
import org.homio.api.video.AbilityToStreamHLSOverFFMPEG;
import org.homio.api.video.BaseFFMPEGVideoStreamEntity;
import org.homio.api.video.BaseVideoService;
import org.homio.addon.camera.service.CommonVideoService;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@Entity
@Accessors(chain = true)
public class CommonVideoStreamEntity extends BaseFFMPEGVideoStreamEntity<CommonVideoStreamEntity, CommonVideoService>
    implements AbilityToStreamHLSOverFFMPEG<CommonVideoStreamEntity> {

  public static final String PREFIX = "vidc_";

  @Override
  @UIField(order = 5, label = "url", inlineEdit = true, required = true)
  @RestartHandlerOnChange
  public String getIeeeAddress() {
    return super.getIeeeAddress();
  }

  @Override
  public String getFolderName() {
    return "video";
  }

  @Override
  public String getDefaultName() {
    return null;
  }

  @Override
  public String toString() {
    return getIeeeAddress();
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  @Override
  protected void beforePersist() {
    setSnapshotOutOptions("-update 1~~~-frames:v 1");
    setServerPort(BaseVideoService.findFreeBootstrapServerPort());
  }

  @Override
  public @NotNull Icon getIcon() {
    return new Icon("fas fa-film", "#4E783D");
  }

  @Override
  public Class<CommonVideoService> getEntityServiceItemClass() {
    return CommonVideoService.class;
  }

  @Override
  public CommonVideoService createService(EntityContext entityContext) {
    return new CommonVideoService(entityContext, this);
  }
}
