package org.onvif.ver20.ptz.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="NodeToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"nodeToken"})
@XmlRootElement(name = "GetNode")
public class GetNode {

  @XmlElement(name = "NodeToken", required = true)
  protected String nodeToken;

  /**
   * Ruft den Wert der nodeToken-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getNodeToken() {
    return nodeToken;
  }

  /**
   * Legt den Wert der nodeToken-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setNodeToken(String value) {
    this.nodeToken = value;
  }

}
