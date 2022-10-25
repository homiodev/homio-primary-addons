package org.onvif.ver20.imaging.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "videoSourceToken"
})
@XmlRootElement(name = "GetOptions")
public class GetOptions {

  @XmlElement(name = "VideoSourceToken", required = true)
  protected String videoSourceToken;

  /**
   * Ruft den Wert der videoSourceToken-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getVideoSourceToken() {
    return videoSourceToken;
  }

  /**
   * Legt den Wert der videoSourceToken-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setVideoSourceToken(String value) {
    this.videoSourceToken = value;
  }

}
