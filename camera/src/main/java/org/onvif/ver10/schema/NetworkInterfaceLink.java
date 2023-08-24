package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r NetworkInterfaceLink complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="NetworkInterfaceLink">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="AdminSettings" type="{http://www.onvif.org/ver10/schema}NetworkInterfaceConnectionSetting"/>
 *         <element name="OperSettings" type="{http://www.onvif.org/ver10/schema}NetworkInterfaceConnectionSetting"/>
 *         <element name="InterfaceType" type="{http://www.onvif.org/ver10/schema}IANA-IfTypes"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "NetworkInterfaceLink",
        propOrder = {"adminSettings", "operSettings", "interfaceType"})
public class NetworkInterfaceLink {

    /**
     * -- GETTER --
     *  Ruft den Wert der adminSettings-Eigenschaft ab.
     *
     * @return possible object is {@link NetworkInterfaceConnectionSetting }
     */
    @XmlElement(name = "AdminSettings", required = true)
    protected NetworkInterfaceConnectionSetting adminSettings;

    /**
     * -- GETTER --
     *  Ruft den Wert der operSettings-Eigenschaft ab.
     *
     * @return possible object is {@link NetworkInterfaceConnectionSetting }
     */
    @XmlElement(name = "OperSettings", required = true)
    protected NetworkInterfaceConnectionSetting operSettings;

    /**
     * -- GETTER --
     *  Ruft den Wert der interfaceType-Eigenschaft ab.
     */
    @XmlElement(name = "InterfaceType")
    protected int interfaceType;

    /**
     * Legt den Wert der adminSettings-Eigenschaft fest.
     *
     * @param value allowed object is {@link NetworkInterfaceConnectionSetting }
     */
    public void setAdminSettings(NetworkInterfaceConnectionSetting value) {
        this.adminSettings = value;
    }

    /**
     * Legt den Wert der operSettings-Eigenschaft fest.
     *
     * @param value allowed object is {@link NetworkInterfaceConnectionSetting }
     */
    public void setOperSettings(NetworkInterfaceConnectionSetting value) {
        this.operSettings = value;
    }

    /**
     * Legt den Wert der interfaceType-Eigenschaft fest.
     */
    public void setInterfaceType(int value) {
        this.interfaceType = value;
    }
}
