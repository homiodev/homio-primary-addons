package org.onvif.ver20.media.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "profileToken",
    "name",
    "configuration"
})
@XmlRootElement(name = "AddConfiguration")
public class AddConfiguration {

  @XmlElement(name = "ProfileToken", required = true)
  protected String profileToken;
  @XmlElement(name = "Name")
  protected String name;
  @XmlElement(name = "Configuration")
  protected List<ConfigurationRef> configuration;

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
   * Ruft den Wert der name-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Legt den Wert der name-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the configuration property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the configuration property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   * <pre>
   *    getConfiguration().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link ConfigurationRef }
   */
  public List<ConfigurationRef> getConfiguration() {
    if (configuration == null) {
      configuration = new ArrayList<ConfigurationRef>();
    }
    return this.configuration;
  }

}
