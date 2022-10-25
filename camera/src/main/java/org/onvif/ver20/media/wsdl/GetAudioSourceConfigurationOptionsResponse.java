package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.AudioSourceConfigurationOptions;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "options"
})
@XmlRootElement(name = "GetAudioSourceConfigurationOptionsResponse")
public class GetAudioSourceConfigurationOptionsResponse {

  @XmlElement(name = "Options", required = true)
  protected AudioSourceConfigurationOptions options;

  /**
   * Ruft den Wert der options-Eigenschaft ab.
   *
   * @return possible object is {@link AudioSourceConfigurationOptions }
   */
  public AudioSourceConfigurationOptions getOptions() {
    return options;
  }

  /**
   * Legt den Wert der options-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioSourceConfigurationOptions }
   */
  public void setOptions(AudioSourceConfigurationOptions value) {
    this.options = value;
  }

}
