package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r WideDynamicRange complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="WideDynamicRange">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}WideDynamicMode"/>
 *         <element name="Level" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WideDynamicRange", propOrder = {"mode", "level"})
public class WideDynamicRange {

  @XmlElement(name = "Mode", required = true)
  protected WideDynamicMode mode;
  @XmlElement(name = "Level")
  protected float level;

  /**
   * Ruft den Wert der mode-Eigenschaft ab.
   *
   * @return possible object is {@link WideDynamicMode }
   */
  public WideDynamicMode getMode() {
    return mode;
  }

  /**
   * Legt den Wert der mode-Eigenschaft fest.
   *
   * @param value allowed object is {@link WideDynamicMode }
   */
  public void setMode(WideDynamicMode value) {
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
