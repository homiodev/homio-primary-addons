package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Config", propOrder = {"parameters"})
public class Config {

  @XmlElement(name = "Parameters", required = true)
  protected ItemList parameters;
  @XmlAttribute(name = "Name", required = true)
  protected String name;
  @XmlAttribute(name = "Type", required = true)
  protected QName type;

  /**
   * Ruft den Wert der parameters-Eigenschaft ab.
   *
   * @return possible object is {@link ItemList }
   */
  public ItemList getParameters() {
    return parameters;
  }

  /**
   * Legt den Wert der parameters-Eigenschaft fest.
   *
   * @param value allowed object is {@link ItemList }
   */
  public void setParameters(ItemList value) {
    this.parameters = value;
  }

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
   * Ruft den Wert der type-Eigenschaft ab.
   *
   * @return possible object is {@link QName }
   */
  public QName getType() {
    return type;
  }

  /**
   * Legt den Wert der type-Eigenschaft fest.
   *
   * @param value allowed object is {@link QName }
   */
  public void setType(QName value) {
    this.type = value;
  }

}
