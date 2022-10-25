package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigurationRef", propOrder = {
    "type",
    "token"
})
public class ConfigurationRef {

  @XmlElement(name = "Type", required = true)
  protected String type;
  @XmlElement(name = "Token")
  protected String token;

  /**
   * Ruft den Wert der type-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getType() {
    return type;
  }

  /**
   * Legt den Wert der type-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setType(String value) {
    this.type = value;
  }

  /**
   * Ruft den Wert der token-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getToken() {
    return token;
  }

  /**
   * Legt den Wert der token-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setToken(String value) {
    this.token = value;
  }

}
