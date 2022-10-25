package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r Mpeg4Configuration complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="Mpeg4Configuration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="GovLength" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="Mpeg4Profile" type="{http://www.onvif.org/ver10/schema}Mpeg4Profile"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Mpeg4Configuration", propOrder = {"govLength", "mpeg4Profile"})
public class Mpeg4Configuration {

  @XmlElement(name = "GovLength")
  protected int govLength;
  @XmlElement(name = "Mpeg4Profile", required = true)
  protected Mpeg4Profile mpeg4Profile;

  /**
   * Ruft den Wert der govLength-Eigenschaft ab.
   */
  public int getGovLength() {
    return govLength;
  }

  /**
   * Legt den Wert der govLength-Eigenschaft fest.
   */
  public void setGovLength(int value) {
    this.govLength = value;
  }

  /**
   * Ruft den Wert der mpeg4Profile-Eigenschaft ab.
   *
   * @return possible object is {@link Mpeg4Profile }
   */
  public Mpeg4Profile getMpeg4Profile() {
    return mpeg4Profile;
  }

  /**
   * Legt den Wert der mpeg4Profile-Eigenschaft fest.
   *
   * @param value allowed object is {@link Mpeg4Profile }
   */
  public void setMpeg4Profile(Mpeg4Profile value) {
    this.mpeg4Profile = value;
  }

}
