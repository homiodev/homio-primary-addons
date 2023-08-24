//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r DeviceServiceCapabilities complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="DeviceServiceCapabilities">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Network" type="{http://www.onvif.org/ver10/device/wsdl}NetworkCapabilities"/>
 *         <element name="Security" type="{http://www.onvif.org/ver10/device/wsdl}SecurityCapabilities"/>
 *         <element name="System" type="{http://www.onvif.org/ver10/device/wsdl}SystemCapabilities"/>
 *         <element name="Misc" type="{http://www.onvif.org/ver10/device/wsdl}MiscCapabilities" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "DeviceServiceCapabilities",
        propOrder = {"network", "security", "system", "misc"})
public class DeviceServiceCapabilities {

    /**
     * -- GETTER --
     *  Ruft den Wert der network-Eigenschaft ab.
     *
     * @return possible object is {@link NetworkCapabilities }
     */
    @XmlElement(name = "Network", required = true)
    protected NetworkCapabilities network;

    /**
     * -- GETTER --
     *  Ruft den Wert der security-Eigenschaft ab.
     *
     * @return possible object is {@link SecurityCapabilities }
     */
    @XmlElement(name = "Security", required = true)
    protected SecurityCapabilities security;

    /**
     * -- GETTER --
     *  Ruft den Wert der system-Eigenschaft ab.
     *
     * @return possible object is {@link SystemCapabilities }
     */
    @XmlElement(name = "System", required = true)
    protected SystemCapabilities system;

    /**
     * -- GETTER --
     *  Ruft den Wert der misc-Eigenschaft ab.
     *
     * @return possible object is {@link MiscCapabilities }
     */
    @XmlElement(name = "Misc")
    protected MiscCapabilities misc;

    /**
     * Legt den Wert der network-Eigenschaft fest.
     *
     * @param value allowed object is {@link NetworkCapabilities }
     */
    public void setNetwork(NetworkCapabilities value) {
        this.network = value;
    }

    /**
     * Legt den Wert der security-Eigenschaft fest.
     *
     * @param value allowed object is {@link SecurityCapabilities }
     */
    public void setSecurity(SecurityCapabilities value) {
        this.security = value;
    }

    /**
     * Legt den Wert der system-Eigenschaft fest.
     *
     * @param value allowed object is {@link SystemCapabilities }
     */
    public void setSystem(SystemCapabilities value) {
        this.system = value;
    }

    /**
     * Legt den Wert der misc-Eigenschaft fest.
     *
     * @param value allowed object is {@link MiscCapabilities }
     */
    public void setMisc(MiscCapabilities value) {
        this.misc = value;
    }
}
