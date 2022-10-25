package org.touchhome.bundle.raspberry.console;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigital;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginTable;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorMatch;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorRef;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorSource;
import org.touchhome.bundle.api.util.RaspberryGpioPin;
import org.touchhome.bundle.raspberry.RaspberryGPIOService;
import org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity;

@Component
@RequiredArgsConstructor
public class GpioConsolePlugin implements ConsolePluginTable<GpioConsolePlugin.GpioPluginEntity> {

  @Getter
  private final EntityContext entityContext;
  private final RaspberryGPIOService raspberryGPIOService;
  private String selectedRaspberry;

  @Override
  public Map<String, Class<? extends ConsoleHeaderSettingPlugin<?>>> getHeaderActions() {
    Map<String, Class<? extends ConsoleHeaderSettingPlugin<?>>> headerActions = new LinkedHashMap<>();
    headerActions.put("gpio.select_raspberry", ConsoleHeaderSelectRaspberryBoardSetting.class);
    return headerActions;
  }

  public void init() {
    entityContext.setting().listenValueAndGet(ConsoleHeaderSelectRaspberryBoardSetting.class, "rasp-board",
        entityId -> this.selectedRaspberry = entityId);
  }

  @Override
  public Collection<GpioPluginEntity> getValue() {
    List<GpioPluginEntity> list = new ArrayList<>();

    entityContext.findAll(RaspberryDeviceEntity.class);

    for (RaspberryGpioPin gpioPin : RaspberryGpioPin.values()) {
      GpioPin pin = raspberryGPIOService.getGpioPin(gpioPin);
      GpioPluginEntity gpioPluginEntity = new GpioPluginEntity();
      gpioPluginEntity.setAddress(gpioPin.getAddress());
      gpioPluginEntity.setName(gpioPin.name());
      gpioPluginEntity.setDescription(gpioPin.getName());
      gpioPluginEntity.setRaspiPin(gpioPin.getPin().getName());
      gpioPluginEntity.setBcmPin(gpioPin.getBcmPin() == null ? null : gpioPin.getBcmPin().getName());
      gpioPluginEntity.setSupportedModes(gpioPin.getPin().getSupportedPinModes().stream().map(p -> p.getName().toUpperCase()).collect(Collectors.toSet()));
      gpioPluginEntity.setOccupied(gpioPin.getOccupied());
      gpioPluginEntity.setColor(gpioPin.getColor());
      gpioPluginEntity.setSupportedPullResistance(gpioPin.getPin().getSupportedPinPullResistance().stream().map(p -> p.getName().toUpperCase()).collect(Collectors.toSet()));
      if (pin != null) {
        gpioPluginEntity.setValue(pin instanceof GpioPinDigital ? ((GpioPinDigital) pin).getState() : null);
        gpioPluginEntity.setMode(pin.getMode());
        gpioPluginEntity.setPullResistance(pin.getPullResistance());
      }
      list.add(gpioPluginEntity);
    }

    Collections.sort(list);
    return list;
  }

  @Override
  public int order() {
    return 2000;
  }

  @Override
  public boolean isEnabled() {
    return entityContext.isFeatureEnabled("GPIO");
  }

  @Override
  public String getName() {
    return "gpio";
  }

  @Override
  public Class<GpioPluginEntity> getEntityClass() {
    return GpioPluginEntity.class;
  }

  @Getter
  @Setter
  public static class GpioPluginEntity implements HasEntityIdentifier, Comparable<GpioPluginEntity> {

    @UIField(order = 1, label = "№")
    private int address;
    @UIField(order = 2)
    @UIFieldColorRef("color")
    private String name;
    @UIField(order = 3)
    private String description;
    @UIField(order = 4, label = "Raspi Pin")
    private String raspiPin;
    @UIField(order = 5, label = "BCM Pin")
    private String bcmPin;
    @UIField(order = 6, label = "Mode")
    private PinMode mode;
    @UIField(order = 7, label = "Occupied")
    private String occupied;
    @UIField(order = 8, label = "Supported modes")
    private Set<String> supportedModes;
    @UIField(order = 9, label = "Pull Resistance")
    private PinPullResistance pullResistance;
    @UIField(order = 10, label = "Supported Pull Resistance")
    private Set<String> supportedPullResistance;
    @UIField(order = 11)
    @UIFieldColorMatch(value = "HIGH", color = "#1F8D2D")
    @UIFieldColorMatch(value = "LOW", color = "#B22020")
    private Object value;
    @UIFieldColorSource
    private String color;

    public String getEntityID() {
      return name;
    }

    @UIContextMenuAction("ACTION.PULL_DOWN_RESISTOR")
    public ActionResponseModel pullDownResistor(EntityContext entityContext, GpioPluginEntity gpioPluginEntity) {
      return setPinPullResistance(gpioPluginEntity, entityContext, PinPullResistance.PULL_DOWN);
    }

    @UIContextMenuAction("ACTION.PULL_UP_RESISTOR")
    public ActionResponseModel pullUpResistor(EntityContext entityContext, GpioPluginEntity gpioPluginEntity) {
      return setPinPullResistance(gpioPluginEntity, entityContext, PinPullResistance.PULL_UP);
    }

    @UIContextMenuAction("ACTION.SET_HIGH")
    public ActionResponseModel setHighState(EntityContext entityContext, GpioPluginEntity gpioPluginEntity) {
      return setPinState(gpioPluginEntity, entityContext, PinState.HIGH);
    }

    @UIContextMenuAction("ACTION.SET_LOW")
    public ActionResponseModel setLowState(EntityContext entityContext, GpioPluginEntity gpioPluginEntity) {
      return setPinState(gpioPluginEntity, entityContext, PinState.LOW);
    }

    @Override
    public int compareTo(@NotNull GpioPluginEntity o) {
      return this.name.compareTo(o.name);
    }

    private ActionResponseModel setPinPullResistance(GpioPluginEntity gpioPluginEntity, EntityContext entityContext,
        PinPullResistance pinPullResistance) {
      RaspberryGpioPin gpioPin = RaspberryGpioPin.fromValue(gpioPluginEntity.getRaspiPin());
      entityContext.getBean(RaspberryGPIOService.class).setPullResistance(gpioPin, pinPullResistance);
      return ActionResponseModel.showSuccess("success");
    }

    private ActionResponseModel setPinState(GpioPluginEntity gpioPluginEntity, EntityContext entityContext,
        PinState pinState) {
      RaspberryGpioPin gpioPin = RaspberryGpioPin.fromValue(gpioPluginEntity.getRaspiPin());
      entityContext.getBean(RaspberryGPIOService.class).setValue(gpioPin, pinState);
      return ActionResponseModel.showSuccess("success");
    }
  }
}
