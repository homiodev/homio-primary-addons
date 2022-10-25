package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r RelativeFocus complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="RelativeFocus">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Distance" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="Speed" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelativeFocus", propOrder = {"distance", "speed"})
public class RelativeFocus {

  @XmlElement(name = "Distance")
  protected float distance;
  @XmlElement(name = "Speed")
  protected Float speed;

  /**
   * Ruft den Wert der distance-Eigenschaft ab.
   */
  public float getDistance() {
    return distance;
  }

  /**
   * Legt den Wert der distance-Eigenschaft fest.
   */
  public void setDistance(float value) {
    this.distance = value;
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
