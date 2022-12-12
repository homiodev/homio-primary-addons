package org.touchhome.bundle.raspberry.gpio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pi4j.io.gpio.digital.PullResistance;
import java.util.Set;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.touchhome.bundle.api.entity.PinBaseEntity;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldColorPicker;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.condition.UIFieldDisableEditOnCondition;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntityEditWidth;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntityWidth;
import org.touchhome.bundle.raspberry.RaspberryDeviceEntity;
import org.touchhome.bundle.raspberry.RaspberryGpioPin;
import org.touchhome.bundle.raspberry.gpio.mode.PinMode;

@Getter
@Setter
@Entity
public class GpioPinEntity extends PinBaseEntity<GpioPinEntity, RaspberryDeviceEntity> {

  @Override
  @UIField(order = 10, label = "pin", disableEdit = true)
  @UIFieldInlineEntityWidth(15)
  @UIFieldInlineEntityEditWidth(15)
  public int getAddress() {
    return super.getAddress();
  }

  @Override
  @UIField(order = 20, disableEdit = true)
  @UIFieldInlineEntityWidth(25)
  @UIFieldInlineEntityEditWidth(15)
  public String getName() {
    return super.getName();
  }

  @UIField(order = 30)
  @UIFieldInlineEntityWidth(20)
  @UIFieldInlineEntityEditWidth(25)
  public PinMode getMode() {
    return getJsonDataEnum("mode", PinMode.DIGITAL_INPUT);
  }

  public void setMode(PinMode value) {
    setJsonDataEnum("mode", value);
  }

  @UIField(order = 40)
  @UIFieldInlineEntityWidth(20)
  @UIFieldInlineEntityEditWidth(25)
  @UIFieldDisableEditOnCondition("return context.get('mode') != 'DIGITAL_INPUT'")
  public PullResistance getPull() {
    return getJsonDataEnum("pull", PullResistance.OFF);
  }

  public void setPull(PullResistance value) {
    setJsonDataEnum("pull", value);
  }

  @UIField(order = 50, hideInEdit = true)
  @UIFieldInlineEntityWidth(20)
  public String getValue() {
    if (getMode() == PinMode.DIGITAL_INPUT) {
      RaspberryDeviceEntity entity = getOwnerTarget();
      if (entity != null) {
        return entity.optService().map(service -> service.getState(getAddress()).stringValue()).orElse(null);
      }
    }
    return null;
  }

  @UIField(order = 60, hideInView = true)
  @UIFieldColorPicker
  @UIFieldInlineEntityEditWidth(25)
  public String getColor() {
    return getJsonData("clr", "");
  }

  public void setColor(String value) {
    setJsonData("clr", value);
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

  public Set<String> setSupportedModes() {
    return getJsonDataSet("modes");
  }

  public void setSupportedModes(String modes) {
    setJsonData("modes", modes);
  }
}
