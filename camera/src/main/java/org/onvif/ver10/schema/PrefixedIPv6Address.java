package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java-Klasse f�r PrefixedIPv6Address complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="PrefixedIPv6Address">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Address" type="{http://www.onvif.org/ver10/schema}IPv6Address"/>
 *         <element name="PrefixLength" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrefixedIPv6Address", propOrder = {"address", "prefixLength"})
public class PrefixedIPv6Address {

  @XmlElement(name = "Address", required = true)
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  protected String address;
  @XmlElement(name = "PrefixLength")
  protected int prefixLength;

  /**
   * Ruft den Wert der address-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getAddress() {
    return address;
  }

  /**
   * Legt den Wert der address-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setAddress(String value) {
    this.address = value;
  }

  /**
   * Ruft den Wert der prefixLength-Eigenschaft ab.
   */
  public int getPrefixLength() {
    return prefixLength;
  }

  /**
   * Legt den Wert der prefixLength-Eigenschaft fest.
   */
  public void setPrefixLength(int value) {
    this.prefixLength = value;
  }

}
