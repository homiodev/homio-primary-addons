package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3._2004._08.xop.include.Include;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttachmentData", propOrder = {"include"})
public class AttachmentData {

  @XmlElement(name = "Include", namespace = "http://www.w3.org/2004/08/xop/include", required = true)
  protected Include include;
  @XmlAttribute(name = "contentType", namespace = "http://www.w3.org/2005/05/xmlmime")
  protected String contentType;

  /**
   * Ruft den Wert der include-Eigenschaft ab.
   *
   * @return possible object is {@link Include }
   */
  public Include getInclude() {
    return include;
  }

  /**
   * Legt den Wert der include-Eigenschaft fest.
   *
   * @param value allowed object is {@link Include }
   */
  public void setInclude(Include value) {
    this.include = value;
  }

  /**
   * Ruft den Wert der contentType-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Legt den Wert der contentType-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setContentType(String value) {
    this.contentType = value;
  }

}
