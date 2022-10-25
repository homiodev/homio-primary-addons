package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateUsage", propOrder = {"value"})
public class CertificateUsage {

  @XmlValue
  protected String value;
  @XmlAttribute(name = "Critical", required = true)
  protected boolean critical;

  /**
   * Ruft den Wert der value-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getValue() {
    return value;
  }

  /**
   * Legt den Wert der value-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Ruft den Wert der critical-Eigenschaft ab.
   */
  public boolean isCritical() {
    return critical;
  }

  /**
   * Legt den Wert der critical-Eigenschaft fest.
   */
  public void setCritical(boolean value) {
    this.critical = value;
  }

}
