package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "osdToken"
})
@XmlRootElement(name = "DeleteOSD")
public class DeleteOSD {

  @XmlElement(name = "OSDToken", required = true)
  protected String osdToken;

  /**
   * Ruft den Wert der osdToken-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getOSDToken() {
    return osdToken;
  }

  /**
   * Legt den Wert der osdToken-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setOSDToken(String value) {
    this.osdToken = value;
  }

}
