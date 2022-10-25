package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbsoluteFocus", propOrder = {"position", "speed"})
public class AbsoluteFocus {

  @XmlElement(name = "Position")
  protected float position;
  @XmlElement(name = "Speed")
  protected Float speed;

  /**
   * Ruft den Wert der position-Eigenschaft ab.
   */
  public float getPosition() {
    return position;
  }

  /**
   * Legt den Wert der position-Eigenschaft fest.
   */
  public void setPosition(float value) {
    this.position = value;
  }

  /**
   * Ruft den Wert der speed-Eigenschaft ab.
   *
   * @return possible object is {@link Float }
   */
  public Float getSpeed() {
    return speed;
  }

  /**
   * Legt den Wert der speed-Eigenschaft fest.
   *
   * @param value allowed object is {@link Float }
   */
  public void setSpeed(Float value) {
    this.speed = value;
  }

}
