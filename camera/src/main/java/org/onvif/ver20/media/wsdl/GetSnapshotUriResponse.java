package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uri"
})
@XmlRootElement(name = "GetSnapshotUriResponse")
public class GetSnapshotUriResponse {

  @XmlElement(name = "Uri", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String uri;

  /**
   * Ruft den Wert der uri-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getUri() {
    return uri;
  }

  /**
   * Legt den Wert der uri-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setUri(String value) {
    this.uri = value;
  }

}
