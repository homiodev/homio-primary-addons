package org.onvif.ver20.imaging.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.ImagingStatus20;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "status"
})
@XmlRootElement(name = "GetStatusResponse")
public class GetStatusResponse {

  @XmlElement(name = "Status", required = true)
  protected ImagingStatus20 status;

  /**
   * Ruft den Wert der status-Eigenschaft ab.
   *
   * @return possible object is {@link ImagingStatus20 }
   */
  public ImagingStatus20 getStatus() {
    return status;
  }

  /**
   * Legt den Wert der status-Eigenschaft fest.
   *
   * @param value allowed object is {@link ImagingStatus20 }
   */
  public void setStatus(ImagingStatus20 value) {
    this.status = value;
  }

}
