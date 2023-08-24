package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java-Klasse f�r MulticastConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="MulticastConfiguration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Address" type="{http://www.onvif.org/ver10/schema}IPAddress"/>
 *         <element name="Port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="TTL" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="AutoStart" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MulticastConfiguration",
        propOrder = {"address", "port", "ttl", "autoStart", "any"})
public class MulticastConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der address-Eigenschaft ab.
     *
     * @return possible object is {@link IPAddress }
     */
    @Getter @XmlElement(name = "Address", required = true)
    protected IPAddress address;

    /**
     * -- GETTER --
     *  Ruft den Wert der port-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "Port")
    protected int port;

    @XmlElement(name = "TTL")
    protected int ttl;

    /**
     * -- GETTER --
     *  Ruft den Wert der autoStart-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "AutoStart")
    protected boolean autoStart;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    /**
     * -- GETTER --
     *  Gets a map that contains attributes that aren't bound to any typed property on this class.
     *  <p>the map is keyed by the name of the attribute and the value is the string value of the
     *  attribute.
     *  <p>the map returned by this method is live, and you can add new attribute by updating the map
     *  directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Legt den Wert der address-Eigenschaft fest.
     *
     * @param value allowed object is {@link IPAddress }
     */
    public void setAddress(IPAddress value) {
        this.address = value;
    }

    /**
     * Legt den Wert der port-Eigenschaft fest.
     */
    public void setPort(int value) {
        this.port = value;
    }

    /**
     * Ruft den Wert der ttl-Eigenschaft ab.
     */
    public int getTTL() {
        return ttl;
    }

    /**
     * Legt den Wert der ttl-Eigenschaft fest.
     */
    public void setTTL(int value) {
        this.ttl = value;
    }

    /**
     * Legt den Wert der autoStart-Eigenschaft fest.
     */
    public void setAutoStart(boolean value) {
        this.autoStart = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the any
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Element } {@link
     * java.lang.Object }
     */
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
