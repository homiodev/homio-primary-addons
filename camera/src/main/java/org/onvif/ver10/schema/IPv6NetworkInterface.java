package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r IPv6NetworkInterface complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="IPv6NetworkInterface">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Enabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="Config" type="{http://www.onvif.org/ver10/schema}IPv6Configuration" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IPv6NetworkInterface", propOrder = {"enabled", "config"})
public class IPv6NetworkInterface {

  @XmlElement(name = "Enabled")
  protected boolean enabled;
  @XmlElement(name = "Config")
  protected IPv6Configuration config;

  /**
   * Ruft den Wert der enabled-Eigenschaft ab.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Legt den Wert der enabled-Eigenschaft fest.
   */
  public void setEnabled(boolean value) {
    this.enabled = value;
  }

  /**
   * Ruft den Wert der config-Eigenschaft ab.
   *
   * @return possible object is {@link IPv6Configuration }
   */
  public IPv6Configuration getConfig() {
    return config;
  }

  /**
   * Legt den Wert der config-Eigenschaft fest.
   *
   * @param value allowed object is {@link IPv6Configuration }
   */
  public void setConfig(IPv6Configuration value) {
    this.config = value;
  }

}
