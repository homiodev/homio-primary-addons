package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorCovariance")
public class ColorCovariance {

  @XmlAttribute(name = "XX", required = true)
  protected float xx;
  @XmlAttribute(name = "YY", required = true)
  protected float yy;
  @XmlAttribute(name = "ZZ", required = true)
  protected float zz;
  @XmlAttribute(name = "XY")
  protected Float xy;
  @XmlAttribute(name = "XZ")
  protected Float xz;
  @XmlAttribute(name = "YZ")
  protected Float yz;
  @XmlAttribute(name = "Colorspace")
  @XmlSchemaType(name = "anyURI")
  protected String colorspace;

  /**
   * Ruft den Wert der xx-Eigenschaft ab.
   */
  public float getXX() {
    return xx;
  }

  /**
   * Legt den Wert der xx-Eigenschaft fest.
   */
  public void setXX(float value) {
    this.xx = value;
  }

  /**
   * Ruft den Wert der yy-Eigenschaft ab.
   */
  public float getYY() {
    return yy;
  }

  /**
   * Legt den Wert der yy-Eigenschaft fest.
   */
  public void setYY(float value) {
    this.yy = value;
  }

  /**
   * Ruft den Wert der zz-Eigenschaft ab.
   */
  public float getZZ() {
    return zz;
  }

  /**
   * Legt den Wert der zz-Eigenschaft fest.
   */
  public void setZZ(float value) {
    this.zz = value;
  }

  /**
   * Ruft den Wert der xy-Eigenschaft ab.
   *
   * @return possible object is {@link Float }
   */
  public Float getXY() {
    return xy;
  }

  /**
   * Legt den Wert der xy-Eigenschaft fest.
   *
   * @param value allowed object is {@link Float }
   */
  public void setXY(Float value) {
    this.xy = value;
  }

  /**
   * Ruft den Wert der xz-Eigenschaft ab.
   *
   * @return possible object is {@link Float }
   */
  public Float getXZ() {
    return xz;
  }

  /**
   * Legt den Wert der xz-Eigenschaft fest.
   *
   * @param value allowed object is {@link Float }
   */
  public void setXZ(Float value) {
    this.xz = value;
  }

  /**
   * Ruft den Wert der yz-Eigenschaft ab.
   *
   * @return possible object is {@link Float }
   */
  public Float getYZ() {
    return yz;
  }

  /**
   * Legt den Wert der yz-Eigenschaft fest.
   *
   * @param value allowed object is {@link Float }
   */
  public void setYZ(Float value) {
    this.yz = value;
  }

  /**
   * Ruft den Wert der colorspace-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getColorspace() {
    return colorspace;
  }

  /**
   * Legt den Wert der colorspace-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setColorspace(String value) {
    this.colorspace = value;
  }

}
