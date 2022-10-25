package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BacklightCompensation", propOrder = {"mode", "level"})
public class BacklightCompensation {

  @XmlElement(name = "Mode", required = true)
  protected BacklightCompensationMode mode;
  @XmlElement(name = "Level")
  protected float level;

  /**
   * Ruft den Wert der mode-Eigenschaft ab.
   *
   * @return possible object is {@link BacklightCompensationMode }
   */
  public BacklightCompensationMode getMode() {
    return mode;
  }

  /**
   * Legt den Wert der mode-Eigenschaft fest.
   *
   * @param value allowed object is {@link BacklightCompensationMode }
   */
  public void setMode(BacklightCompensationMode value) {
    this.mode = value;
  }

  /**
   * Ruft den Wert der level-Eigenschaft ab.
   */
  public float getLevel() {
    return level;
  }

  /**
   * Legt den Wert der level-Eigenschaft fest.
   */
  public void setLevel(float value) {
    this.level = value;
  }

}
