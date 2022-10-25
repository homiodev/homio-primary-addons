package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigurationEntity", propOrder = {"name", "useCount"})
@XmlSeeAlso({AudioSourceConfiguration.class, PTZConfiguration.class, VideoSourceConfiguration.class, AudioEncoderConfiguration.class,
    VideoEncoderConfiguration.class, AudioDecoderConfiguration.class, VideoAnalyticsConfiguration.class, AudioOutputConfiguration.class,
    MetadataConfiguration.class, AnalyticsEngineInput.class, AnalyticsEngineControl.class, AnalyticsEngine.class, VideoOutputConfiguration.class})
public class ConfigurationEntity {

  @XmlElement(name = "Name", required = true)
  protected String name;
  @XmlElement(name = "UseCount")
  protected int useCount;
  @XmlAttribute(name = "token", required = true)
  protected String token;

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
   * Ruft den Wert der useCount-Eigenschaft ab.
   */
  public int getUseCount() {
    return useCount;
  }

  /**
   * Legt den Wert der useCount-Eigenschaft fest.
   */
  public void setUseCount(int value) {
    this.useCount = value;
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
