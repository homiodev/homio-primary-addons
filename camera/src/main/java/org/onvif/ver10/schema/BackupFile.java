package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackupFile", propOrder = {"name", "data"})
public class BackupFile {

  @XmlElement(name = "Name", required = true)
  protected String name;
  @XmlElement(name = "Data", required = true)
  protected AttachmentData data;

  /**
   * Ruft den Wert der name-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Legt den Wert der name-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Ruft den Wert der data-Eigenschaft ab.
   *
   * @return possible object is {@link AttachmentData }
   */
  public AttachmentData getData() {
    return data;
  }

  /**
   * Legt den Wert der data-Eigenschaft fest.
   *
   * @param value allowed object is {@link AttachmentData }
   */
  public void setData(AttachmentData value) {
    this.data = value;
  }

}
