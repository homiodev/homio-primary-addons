package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeviceEntity")
@XmlSeeAlso({RelayOutput.class, NetworkInterface.class, VideoOutput.class, AudioSource.class, OSDConfiguration.class, AudioOutput.class, VideoSource.class,
    DigitalInput.class, PTZNode.class})
public class DeviceEntity {

  @XmlAttribute(name = "token", required = true)
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
