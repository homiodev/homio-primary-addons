package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetConfiguration", propOrder = {
    "configurationToken",
    "profileToken"
})
public class GetConfiguration {

  @XmlElement(name = "ConfigurationToken")
  protected String configurationToken;
  @XmlElement(name = "ProfileToken")
  protected String profileToken;

  /**
   * Ruft den Wert der configurationToken-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getConfigurationToken() {
    return configurationToken;
  }

  /**
   * Legt den Wert der configurationToken-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setConfigurationToken(String value) {
    this.configurationToken = value;
  }

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
