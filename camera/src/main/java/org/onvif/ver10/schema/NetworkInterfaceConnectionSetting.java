package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r NetworkInterfaceConnectionSetting complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="NetworkInterfaceConnectionSetting">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="AutoNegotiation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="Speed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="Duplex" type="{http://www.onvif.org/ver10/schema}Duplex"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "NetworkInterfaceConnectionSetting",
        propOrder = {"autoNegotiation", "speed", "duplex"})
public class NetworkInterfaceConnectionSetting {

    /**
     * -- GETTER --
     *  Ruft den Wert der autoNegotiation-Eigenschaft ab.
     */
    @XmlElement(name = "AutoNegotiation")
    protected boolean autoNegotiation;

    /**
     * -- GETTER --
     *  Ruft den Wert der speed-Eigenschaft ab.
     */
    @XmlElement(name = "Speed")
    protected int speed;

    /**
     * -- GETTER --
     *  Ruft den Wert der duplex-Eigenschaft ab.
     *
     * @return possible object is {@link Duplex }
     */
    @XmlElement(name = "Duplex", required = true)
    protected Duplex duplex;

    /**
     * Legt den Wert der autoNegotiation-Eigenschaft fest.
     */
    public void setAutoNegotiation(boolean value) {
        this.autoNegotiation = value;
    }

    /**
     * Legt den Wert der speed-Eigenschaft fest.
     */
    public void setSpeed(int value) {
        this.speed = value;
    }

    /**
     * Legt den Wert der duplex-Eigenschaft fest.
     *
     * @param value allowed object is {@link Duplex }
     */
    public void setDuplex(Duplex value) {
        this.duplex = value;
    }
}
