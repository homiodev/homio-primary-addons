package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "token"
})
@XmlRootElement(name = "DeleteProfile")
public class DeleteProfile {

  @XmlElement(name = "Token", required = true)
  protected String token;

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
