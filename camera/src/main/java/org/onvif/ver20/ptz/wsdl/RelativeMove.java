package org.onvif.ver20.ptz.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.PTZSpeed;
import org.onvif.ver10.schema.PTZVector;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"profileToken", "translation", "speed"})
@XmlRootElement(name = "RelativeMove")
public class RelativeMove {

  @XmlElement(name = "ProfileToken", required = true)
  protected String profileToken;
  @XmlElement(name = "Translation", required = true)
  protected PTZVector translation;
  @XmlElement(name = "Speed")
  protected PTZSpeed speed;

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

  /**
   * Ruft den Wert der translation-Eigenschaft ab.
   *
   * @return possible object is {@link PTZVector }
   */
  public PTZVector getTranslation() {
    return translation;
  }

  /**
   * Legt den Wert der translation-Eigenschaft fest.
   *
   * @param value allowed object is {@link PTZVector }
   */
  public void setTranslation(PTZVector value) {
    this.translation = value;
  }

  /**
   * Ruft den Wert der speed-Eigenschaft ab.
   *
   * @return possible object is {@link PTZSpeed }
   */
  public PTZSpeed getSpeed() {
    return speed;
  }

  /**
   * Legt den Wert der speed-Eigenschaft fest.
   *
   * @param value allowed object is {@link PTZSpeed }
   */
  public void setSpeed(PTZSpeed value) {
    this.speed = value;
  }

}
