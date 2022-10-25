package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.OSDConfigurationOptions;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "osdOptions"
})
@XmlRootElement(name = "GetOSDOptionsResponse")
public class GetOSDOptionsResponse {

  @XmlElement(name = "OSDOptions", required = true)
  protected OSDConfigurationOptions osdOptions;

  /**
   * Ruft den Wert der osdOptions-Eigenschaft ab.
   *
   * @return possible object is {@link OSDConfigurationOptions }
   */
  public OSDConfigurationOptions getOSDOptions() {
    return osdOptions;
  }

  /**
   * Legt den Wert der osdOptions-Eigenschaft fest.
   *
   * @param value allowed object is {@link OSDConfigurationOptions }
   */
  public void setOSDOptions(OSDConfigurationOptions value) {
    this.osdOptions = value;
  }

}
