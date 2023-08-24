package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;

/**
 * Java-Klasse f�r IPAddress complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="IPAddress">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Type" type="{http://www.onvif.org/ver10/schema}IPType"/>
 *         <element name="IPv4Address" type="{http://www.onvif.org/ver10/schema}IPv4Address" minOccurs="0"/>
 *         <element name="IPv6Address" type="{http://www.onvif.org/ver10/schema}IPv6Address" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "IPAddress",
        propOrder = {"type", "iPv4Address", "iPv6Address"})
public class IPAddress {

    /**
     * -- GETTER --
     *  Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is {@link IPType }
     */
    @Getter @XmlElement(name = "Type", required = true)
    protected IPType type;

    @XmlElement(name = "IPv4Address")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String iPv4Address;

    @XmlElement(name = "IPv6Address")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String iPv6Address;

    /**
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is {@link IPType }
     */
    public void setType(IPType value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der iPv4Address-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getIPv4Address() {
        return iPv4Address;
    }

    /**
     * Legt den Wert der iPv4Address-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setIPv4Address(String value) {
        this.iPv4Address = value;
    }

    /**
     * Ruft den Wert der iPv6Address-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getIPv6Address() {
        return iPv6Address;
    }

    /**
     * Legt den Wert der iPv6Address-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setIPv6Address(String value) {
        this.iPv6Address = value;
    }
}
