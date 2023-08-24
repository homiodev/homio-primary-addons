package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r Transport complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Transport">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Protocol" type="{http://www.onvif.org/ver10/schema}TransportProtocol"/>
 *         <element name="Tunnel" type="{http://www.onvif.org/ver10/schema}Transport" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Transport",
        propOrder = {"protocol", "tunnel"})
public class Transport {

    /**
     * -- GETTER --
     *  Ruft den Wert der protocol-Eigenschaft ab.
     *
     * @return possible object is {@link TransportProtocol }
     */
    @XmlElement(name = "Protocol", required = true)
    protected TransportProtocol protocol;

    /**
     * -- GETTER --
     *  Ruft den Wert der tunnel-Eigenschaft ab.
     *
     * @return possible object is {@link Transport }
     */
    @XmlElement(name = "Tunnel")
    protected Transport tunnel;

    /**
     * Legt den Wert der protocol-Eigenschaft fest.
     *
     * @param value allowed object is {@link TransportProtocol }
     */
    public void setProtocol(TransportProtocol value) {
        this.protocol = value;
    }

    /**
     * Legt den Wert der tunnel-Eigenschaft fest.
     *
     * @param value allowed object is {@link Transport }
     */
    public void setTunnel(Transport value) {
        this.tunnel = value;
    }
}
