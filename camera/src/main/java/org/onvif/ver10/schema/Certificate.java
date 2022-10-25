package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Certificate", propOrder = {"certificateID", "certificate"})
public class Certificate {

  @XmlElement(name = "CertificateID", required = true)
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String certificateID;
  @XmlElement(name = "Certificate", required = true)
  protected BinaryData certificate;

  /**
   * Ruft den Wert der certificateID-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getCertificateID() {
    return certificateID;
  }

  /**
   * Legt den Wert der certificateID-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setCertificateID(String value) {
    this.certificateID = value;
  }

  /**
   * Ruft den Wert der certificate-Eigenschaft ab.
   *
   * @return possible object is {@link BinaryData }
   */
  public BinaryData getCertificate() {
    return certificate;
  }

  /**
   * Legt den Wert der certificate-Eigenschaft fest.
   *
   * @param value allowed object is {@link BinaryData }
   */
  public void setCertificate(BinaryData value) {
    this.certificate = value;
  }

}
