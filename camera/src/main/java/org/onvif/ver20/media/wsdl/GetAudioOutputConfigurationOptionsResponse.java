package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.AudioOutputConfigurationOptions;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "options"
})
@XmlRootElement(name = "GetAudioOutputConfigurationOptionsResponse")
public class GetAudioOutputConfigurationOptionsResponse {

  @XmlElement(name = "Options", required = true)
  protected AudioOutputConfigurationOptions options;

  /**
   * Ruft den Wert der options-Eigenschaft ab.
   *
   * @return possible object is {@link AudioOutputConfigurationOptions }
   */
  public AudioOutputConfigurationOptions getOptions() {
    return options;
  }

  /**
   * Legt den Wert der options-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioOutputConfigurationOptions }
   */
  public void setOptions(AudioOutputConfigurationOptions value) {
    this.options = value;
  }

}
