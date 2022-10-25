package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbsoluteFocusOptions", propOrder = {"position", "speed"})
public class AbsoluteFocusOptions {

  @XmlElement(name = "Position", required = true)
  protected FloatRange position;
  @XmlElement(name = "Speed")
  protected FloatRange speed;

  /**
   * Ruft den Wert der position-Eigenschaft ab.
   *
   * @return possible object is {@link FloatRange }
   */
  public FloatRange getPosition() {
    return position;
  }

  /**
   * Legt den Wert der position-Eigenschaft fest.
   *
   * @param value allowed object is {@link FloatRange }
   */
  public void setPosition(FloatRange value) {
    this.position = value;
  }

  /**
   * Ruft den Wert der speed-Eigenschaft ab.
   *
   * @return possible object is {@link FloatRange }
   */
  public FloatRange getSpeed() {
    return speed;
  }

  /**
   * Legt den Wert der speed-Eigenschaft fest.
   *
   * @param value allowed object is {@link FloatRange }
   */
  public void setSpeed(FloatRange value) {
    this.speed = value;
  }

}
