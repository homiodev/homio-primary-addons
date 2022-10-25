package org.onvif.ver10.schema;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateGenerationParameters", propOrder = {"certificateID", "subject", "validNotBefore", "validNotAfter", "extension"})
public class CertificateGenerationParameters {

  @XmlElement(name = "CertificateID")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String certificateID;
  @XmlElement(name = "Subject")
  protected String subject;
  @XmlElement(name = "ValidNotBefore")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String validNotBefore;
  @XmlElement(name = "ValidNotAfter")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String validNotAfter;
  @XmlElement(name = "Extension")
  protected CertificateGenerationParametersExtension extension;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
   * Ruft den Wert der subject-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Legt den Wert der subject-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setSubject(String value) {
    this.subject = value;
  }

  /**
   * Ruft den Wert der validNotBefore-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getValidNotBefore() {
    return validNotBefore;
  }

  /**
   * Legt den Wert der validNotBefore-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setValidNotBefore(String value) {
    this.validNotBefore = value;
  }

  /**
   * Ruft den Wert der validNotAfter-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getValidNotAfter() {
    return validNotAfter;
  }

  /**
   * Legt den Wert der validNotAfter-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setValidNotAfter(String value) {
    this.validNotAfter = value;
  }

  /**
   * Ruft den Wert der extension-Eigenschaft ab.
   *
   * @return possible object is {@link CertificateGenerationParametersExtension }
   */
  public CertificateGenerationParametersExtension getExtension() {
    return extension;
  }

  /**
   * Legt den Wert der extension-Eigenschaft fest.
   *
   * @param value allowed object is {@link CertificateGenerationParametersExtension }
   */
  public void setExtension(CertificateGenerationParametersExtension value) {
    this.extension = value;
  }

  /**
   * Gets a map that contains attributes that aren't bound to any typed property on this class.
   *
   * <p>
   * the map is keyed by the name of the attribute and the value is the string value of the attribute.
   * <p>
   * the map returned by this method is live, and you can add new attribute by updating the map directly. Because of this design, there's no setter.
   *
   * @return always non-null
   */
  public Map<QName, String> getOtherAttributes() {
    return otherAttributes;
  }

}
