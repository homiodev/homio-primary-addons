package org.touchhome.bundle.raspberry.gpio;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.raspberry.gpio.mode.PinMode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GpioPin implements Comparable<GpioPin> {

  private int address;
  private String description;
  private String name;
  private String color;
  private Set<PinMode> supportModes;

  @Override
  public int compareTo(@NotNull GpioPin o) {
    return this.address - o.address;
  }

  @Override
  public String toString() {
    return "[" + address + "/" + name + "']";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GpioPin gpioPin = (GpioPin) o;

    return address == gpioPin.address;
  }

  @Override
  public int hashCode() {
    return address;
  }
}
