package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.MetadataConfigurationOptions;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "options"
})
@XmlRootElement(name = "GetMetadataConfigurationOptionsResponse")
public class GetMetadataConfigurationOptionsResponse {

  @XmlElement(name = "Options", required = true)
  protected MetadataConfigurationOptions options;

  /**
   * Ruft den Wert der options-Eigenschaft ab.
   *
   * @return possible object is {@link MetadataConfigurationOptions }
   */
  public MetadataConfigurationOptions getOptions() {
    return options;
  }

  /**
   * Legt den Wert der options-Eigenschaft fest.
   *
   * @param value allowed object is {@link MetadataConfigurationOptions }
   */
  public void setOptions(MetadataConfigurationOptions value) {
    this.options = value;
  }

}
