package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "configuration"
})
@XmlRootElement(name = "SetAudioDecoderConfiguration")
public class SetAudioDecoderConfiguration {

  @XmlElement(name = "Configuration", required = true)
  protected AudioDecoderConfiguration configuration;

  /**
   * Ruft den Wert der configuration-Eigenschaft ab.
   *
   * @return possible object is {@link AudioDecoderConfiguration }
   */
  public AudioDecoderConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * Legt den Wert der configuration-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioDecoderConfiguration }
   */
  public void setConfiguration(AudioDecoderConfiguration value) {
    this.configuration = value;
  }

}
