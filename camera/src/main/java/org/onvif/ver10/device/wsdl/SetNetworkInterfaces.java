//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.NetworkInterfaceSetConfiguration;

/**
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="InterfaceToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         <element name="NetworkInterface" type="{http://www.onvif.org/ver10/schema}NetworkInterfaceSetConfiguration"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"interfaceToken", "networkInterface"})
@XmlRootElement(name = "SetNetworkInterfaces")
public class SetNetworkInterfaces {

    /**
     * -- GETTER --
     *  Ruft den Wert der interfaceToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "InterfaceToken", required = true)
    protected String interfaceToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der networkInterface-Eigenschaft ab.
     *
     * @return possible object is {@link NetworkInterfaceSetConfiguration }
     */
    @XmlElement(name = "NetworkInterface", required = true)
    protected NetworkInterfaceSetConfiguration networkInterface;

    /**
     * Legt den Wert der interfaceToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setInterfaceToken(String value) {
        this.interfaceToken = value;
    }

    /**
     * Legt den Wert der networkInterface-Eigenschaft fest.
     *
     * @param value allowed object is {@link NetworkInterfaceSetConfiguration }
     */
    public void setNetworkInterface(NetworkInterfaceSetConfiguration value) {
        this.networkInterface = value;
    }
}
