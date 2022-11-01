package org.touchhome.bundle.raspberry.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.PinMode;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.storage.BaseFileSystemEntity;
import org.touchhome.bundle.api.entity.types.MicroControllerBaseEntity;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldExpand;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.util.RaspberryGpioPin;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.raspberry.RaspberryGPIOService;
import org.touchhome.bundle.raspberry.fs.RaspberryFileSystem;
import org.touchhome.common.util.CommonUtils;

@Entity
public final class RaspberryDeviceEntity extends MicroControllerBaseEntity<RaspberryDeviceEntity>
    implements BaseFileSystemEntity<RaspberryDeviceEntity, RaspberryFileSystem> {

  public static final String PREFIX = "raspb_";
  public static final String DEFAULT_DEVICE_ENTITY_ID = PREFIX + TouchHomeUtils.APP_UUID;

  @Getter
  @Setter
  @Transient
  @UIField(order = 1000, type = UIFieldType.SelectBox, readOnly = true, color = "#7FBBCC")
  @UIFieldExpand
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<AvailableLink> availableLinks;

  @Override
  public String getDefaultName() {
    return "Raspi";
  }

  @UIField(order = 200)
  public String getFileSystemRoot() {
    return getJsonData("fs_root", CommonUtils.getRootPath().toString());
  }

  public void setFileSystemRoot(String value) {
    setJsonData("fs_root", value);
  }

  @Override
  public int getOrder() {
    return 10;
  }

  @Override
  public void afterFetch(EntityContext entityContext) {
    gatherAvailableLinks(entityContext);
  }

  @Override
  protected void beforeDelete() {
    if (getEntityID().equals(DEFAULT_DEVICE_ENTITY_ID)) {
      throw new IllegalStateException("Unable to remove primary Raspberry entity");
    }
    super.beforeDelete();
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  private void gatherAvailableLinks(EntityContext entityContext) {
    List<AvailableLink> links = new ArrayList<>();
    RaspberryGPIOService raspberryGPIOService = entityContext.getBean(RaspberryGPIOService.class);
    for (RaspberryGpioPin gpioPin : RaspberryGpioPin.values(PinMode.DIGITAL_INPUT, null)) {
      AvailableLink link = new AvailableLink(OptionModel.of(gpioPin.name(), gpioPin.toString()).json(json ->
          json
              .put("group", "WorkspaceGroupVariable.PREFIX")
              .put("color", gpioPin.getColor())
              .put("var", "WorkspaceGroupVariable.PREFIX")),
          getLinkedWorkspaceBooleanVariable(gpioPin, entityContext, raspberryGPIOService));
      links.add(link);
    }
    setAvailableLinks(links);
  }

  private String getLinkedWorkspaceBooleanVariable(RaspberryGpioPin gpioPin, EntityContext entityContext,
      RaspberryGPIOService raspberryGPIOService) {
    List<RaspberryGPIOService.PinListener> pinListeners = raspberryGPIOService.getDigitalListeners().get(gpioPin);
    if (pinListeners != null) {
      for (RaspberryGPIOService.PinListener pinListener : pinListeners) {
        if (pinListener.getName().startsWith("WorkspaceGroupVariable.PREFIX")) {
                    /* TODO: WorkspaceGroupVariable variableEntity = entityContext.getEntity(pinListener.getName());
                    if (variableEntity != null) {
                        return variableEntity.getTitle();
                    }*/
        }
      }
    }
    return "";
  }

  @Override
  public String getFileSystemAlias() {
    return "RASPBERRY";
  }

  @Override
  public boolean isShowInFileManager() {
    return true;
  }

  @Override
  public String getFileSystemIcon() {
    return "fa-brands fa-raspberry-pi";
  }

  @Override
  public String getFileSystemIconColor() {
    return "#C70039";
  }

  @Override
  public boolean requireConfigure() {
    return false;
  }

  @Override
  public RaspberryFileSystem buildFileSystem(EntityContext entityContext) {
    return new RaspberryFileSystem(this);
  }

  @Override
  public long getConnectionHashCode() {
    return 0;
  }

  @Override
  public void assembleActions(UIInputBuilder uiInputBuilder) {

  }

  @Getter
  @AllArgsConstructor
  public static final class AvailableLink {

    private OptionModel key;
    private String value;
  }
}
