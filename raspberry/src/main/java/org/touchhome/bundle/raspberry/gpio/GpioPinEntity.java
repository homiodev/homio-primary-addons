package org.touchhome.bundle.raspberry.gpio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pi4j.io.gpio.digital.PullResistance;
import java.util.Date;
import java.util.Set;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.touchhome.bundle.api.entity.PinBaseEntity;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldColorPicker;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldIgnoreGetDefault;
import org.touchhome.bundle.api.ui.field.UIFieldInlineEntityWidth;
import org.touchhome.bundle.api.ui.field.condition.UIFieldDisableEditOnCondition;
import org.touchhome.bundle.raspberry.RaspberryDeviceEntity;
import org.touchhome.bundle.raspberry.RaspberryGpioPin;
import org.touchhome.bundle.raspberry.gpio.mode.PinMode;

@Getter
@Setter
@Entity
public class GpioPinEntity extends PinBaseEntity<RaspberryDeviceEntity> {

  @Override
  @UIField(order = 10, label = "pin", disableEdit = true)
  @UIFieldInlineEntityWidth(editWidth = 15, viewWidth = 15)
  public int getAddress() {
    return super.getAddress();
  }

  @Override
  public String getDefaultName() {
    return null;
  }

  @Override
  @UIField(order = 20, disableEdit = true)
  @UIFieldInlineEntityWidth(editWidth = 15, viewWidth = 25)
  public String getName() {
    return super.getName();
  }

  @UIField(order = 30)
  @UIFieldInlineEntityWidth(editWidth = 25, viewWidth = 20)
  public PinMode getMode() {
    return getJsonDataEnum("mode", PinMode.DIGITAL_INPUT);
  }

  @UIField(order = 40)
  @UIFieldInlineEntityWidth(editWidth = 25, viewWidth = 20)
  @UIFieldDisableEditOnCondition("return context.get('mode') != 'DIGITAL_INPUT'")
  public PullResistance getPull() {
    return getJsonDataEnum("pull", PullResistance.OFF);
  }

  @UIField(order = 50, readOnly = true)
  @UIFieldIgnoreGetDefault
  @UIFieldInlineEntityWidth(editWidth = 0, viewWidth = 20)
  public String getValue() {
    Object owner = getOwner();
    RaspberryDeviceEntity entity;
    if (owner instanceof HibernateProxy) {
      if (((HibernateProxy) owner).getHibernateLazyInitializer().isUninitialized()) {
        return null;
      }
      entity = ((RaspberryDeviceEntity) ((HibernateProxy) owner).getHibernateLazyInitializer().getImplementation());
    } else {
      entity = (RaspberryDeviceEntity) owner;
    }
    if (getMode() == PinMode.DIGITAL_INPUT) {
      return entity.getService().getState(getAddress()).stringValue();
    }
    return null;
  }

  @UIField(order = 60, onlyEdit = true)
  @UIFieldColorPicker
  @UIFieldInlineEntityWidth(editWidth = 25, viewWidth = 0)
  public String getColor() {
    return getJsonData("clr", "");
  }

  @Override
  @UIFieldIgnore
  public String getDescription() {
    return super.getDescription();
  }

  @JsonIgnore
  public GpioPin getGpioPin() {
    return RaspberryGpioPin.getPin(getAddress()).getGpioPin();
  }

  @Override
  @JsonIgnore
  @UIFieldIgnore
  public Date getCreationTime() {
    return super.getCreationTime();
  }

  @Override
  @JsonIgnore
  @UIFieldIgnore
  public Date getUpdateTime() {
    return super.getUpdateTime();
  }

  public void setMode(PinMode value) {
    setJsonDataEnum("mode", value);
  }

  public void setPull(PullResistance value) {
    setJsonDataEnum("pull", value);
  }

  public void setColor(String value) {
    setJsonData("clr", value);
  }

  public Set<String> setSupportedModes() {
    return getJsonDataSet("modes");
  }

  public void setSupportedModes(String modes) {
    setJsonData("modes", modes);
  }
}
