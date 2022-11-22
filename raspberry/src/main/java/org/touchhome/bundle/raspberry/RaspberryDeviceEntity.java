package org.touchhome.bundle.raspberry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.plugin.pigpio.PiGpioPlugin;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.storage.BaseFileSystemEntity;
import org.touchhome.bundle.api.entity.types.MicroControllerBaseEntity;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEditEntities;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntities;
import org.touchhome.bundle.api.util.BoardInfo;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.raspberry.fs.RaspberryFileSystem;
import org.touchhome.bundle.raspberry.gpio.GPIOService;
import org.touchhome.bundle.raspberry.gpio.GpioEntity;
import org.touchhome.bundle.raspberry.gpio.GpioPinEntity;
import org.touchhome.bundle.raspberry.gpio.GpioProviderIdModel;
import org.touchhome.common.util.CommonUtils;

@Entity
public final class RaspberryDeviceEntity extends MicroControllerBaseEntity<RaspberryDeviceEntity>
    implements BaseFileSystemEntity<RaspberryDeviceEntity, RaspberryFileSystem>,
    GpioEntity<RaspberryDeviceEntity> {

  public static final String PREFIX = "raspb_";
  public static final String DEFAULT_DEVICE_ENTITY_ID = PREFIX + TouchHomeUtils.APP_UUID;

  private static final GpioProviderIdModel GPIO_PROVIDER;

  static {
    if (BoardInfo.boardType.equals("UNKNOWN")) {
      GPIO_PROVIDER = new GpioProviderIdModel(
          Mock.DIGITAL_INPUT_PROVIDER_ID,
          Mock.DIGITAL_OUTPUT_PROVIDER_ID,
          Mock.PWM_PROVIDER_ID,
          Mock.ANALOG_INPUT_PROVIDER_ID,
          Mock.ANALOG_OUTPUT_PROVIDER_ID,
          Mock.SPI_PROVIDER_ID,
          Mock.SERIAL_PROVIDER_ID,
          Mock.I2C_PROVIDER_ID
      );
    } else {
      GPIO_PROVIDER = new GpioProviderIdModel(
          PiGpioPlugin.DIGITAL_INPUT_PROVIDER_ID,
          PiGpioPlugin.DIGITAL_OUTPUT_PROVIDER_ID,
          PiGpioPlugin.PWM_PROVIDER_ID,
          null,
          null,
          PiGpioPlugin.SPI_PROVIDER_ID,
          PiGpioPlugin.SERIAL_PROVIDER_ID,
          PiGpioPlugin.I2C_PROVIDER_ID
      );
    }
  }

  @Getter
  @Setter
  @UIField(order = 1000)
  @UIFieldInlineEntities(bg = "#1E5E611F")
  @UIFieldInlineEditEntities(bg = "#1E5E611F", addRowCondition = "return false", removeRowCondition = "return false")
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "owner")
  @OrderBy("position asc")
  private Set<GpioPinEntity> gpioPinEntities;

  @UIField(order = 4)
  @UIFieldSlider(min = 1, max = 120, step = 5, header = "S")
  public int getOneWireInterval() {
    return getJsonData("owi", 30);
  }

  public void setOneWireInterval(int value) {
    setJsonData("owi", value);
  }

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
  public void beforeDelete(EntityContext entityContext) {
    if (getEntityID().equals(DEFAULT_DEVICE_ENTITY_ID)) {
      throw new IllegalStateException("Unable to remove primary Raspberry entity");
    }
    super.beforeDelete(entityContext);
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
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
    return "fab fa-raspberry-pi";
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

  @Override
  public void logBuilder(EntityLogBuilder entityLogBuilder) {
    entityLogBuilder.addTopic("org.touchhome.bundle.raspberry");
  }

  @Override
  public Class<GPIOService> getEntityServiceItemClass() {
    return GPIOService.class;
  }

  @Override
  public @NotNull GPIOService createService(@NotNull EntityContext entityContext) {
    return new GPIOService(entityContext, RaspberryGpioPin.getGpioPins(), this);
  }

  @JsonIgnore
  public GpioProviderIdModel getGpioProvider() {
    return GPIO_PROVIDER;
  }
}
