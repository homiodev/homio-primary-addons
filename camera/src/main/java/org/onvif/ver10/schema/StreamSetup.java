package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

/**
 * <p>
 * Java-Klasse f�r StreamSetup complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="StreamSetup">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Stream" type="{http://www.onvif.org/ver10/schema}StreamType"/>
 *         <element name="Transport" type="{http://www.onvif.org/ver10/schema}Transport"/>
 *         <any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StreamSetup", propOrder = {"stream", "transport", "any"})
public class StreamSetup {

  @XmlElement(name = "Stream", required = true)
  protected StreamType stream;
  @XmlElement(name = "Transport", required = true)
  protected Transport transport;
  @XmlAnyElement(lax = true)
  protected List<java.lang.Object> any;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

  /**
   * Ruft den Wert der stream-Eigenschaft ab.
   *
   * @return possible object is {@link StreamType }
   */
  public StreamType getStream() {
    return stream;
  }

  /**
   * Legt den Wert der stream-Eigenschaft fest.
   *
   * @param value allowed object is {@link StreamType }
   */
  public void setStream(StreamType value) {
    this.stream = value;
  }

  /**
   * Ruft den Wert der transport-Eigenschaft ab.
   *
   * @return possible object is {@link Transport }
   */
  public Transport getTransport() {
    return transport;
  }

  /**
   * Legt den Wert der transport-Eigenschaft fest.
   *
   * @param value allowed object is {@link Transport }
   */
  public void setTransport(Transport value) {
    this.transport = value;
  }

  /**
   * Gets the value of the any property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the any property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getAny().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link Element } {@link java.lang.Object }
   */
  public List<java.lang.Object> getAny() {
    if (any == null) {
      any = new ArrayList<java.lang.Object>();
    }
    return this.any;
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
