package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r Split complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="Split">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="from" type="{http://www.onvif.org/ver10/schema}ObjectId"/>
 *         <element name="to" type="{http://www.onvif.org/ver10/schema}ObjectId" maxOccurs="unbounded" minOccurs="2"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Split", propOrder = {"from", "to"})
public class Split {

  @XmlElement(required = true)
  protected ObjectId from;
  @XmlElement(required = true)
  protected List<ObjectId> to;

  /**
   * Ruft den Wert der from-Eigenschaft ab.
   *
   * @return possible object is {@link ObjectId }
   */
  public ObjectId getFrom() {
    return from;
  }

  /**
   * Legt den Wert der from-Eigenschaft fest.
   *
   * @param value allowed object is {@link ObjectId }
   */
  public void setFrom(ObjectId value) {
    this.from = value;
  }

  /**
   * Gets the value of the to property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the to property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getTo().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link ObjectId }
   */
  public List<ObjectId> getTo() {
    if (to == null) {
      to = new ArrayList<ObjectId>();
    }
    return this.to;
  }

}
