package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContinuousFocus", propOrder = {"speed"})
public class ContinuousFocus {

  @XmlElement(name = "Speed")
  protected float speed;

  /**
   * Ruft den Wert der speed-Eigenschaft ab.
   */
  public float getSpeed() {
    return speed;
  }

  /**
   * Legt den Wert der speed-Eigenschaft fest.
   */
  public void setSpeed(float value) {
    this.speed = value;
  }

}
