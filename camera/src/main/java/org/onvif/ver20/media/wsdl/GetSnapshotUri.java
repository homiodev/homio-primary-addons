package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "profileToken"
})
@XmlRootElement(name = "GetSnapshotUri")
public class GetSnapshotUri {

  @XmlElement(name = "ProfileToken", required = true)
  protected String profileToken;

  /**
   * Ruft den Wert der profileToken-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getProfileToken() {
    return profileToken;
  }

  /**
   * Legt den Wert der profileToken-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setProfileToken(String value) {
    this.profileToken = value;
  }

}
