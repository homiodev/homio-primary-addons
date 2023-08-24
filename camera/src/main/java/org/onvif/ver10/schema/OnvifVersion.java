package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r OnvifVersion complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OnvifVersion">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Major" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="Minor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OnvifVersion",
        propOrder = {"major", "minor"})
public class OnvifVersion {

    /**
     * -- GETTER --
     *  Ruft den Wert der major-Eigenschaft ab.
     */
    @XmlElement(name = "Major")
    protected int major;

    /**
     * -- GETTER --
     *  Ruft den Wert der minor-Eigenschaft ab.
     */
    @XmlElement(name = "Minor")
    protected int minor;

    /**
     * Legt den Wert der major-Eigenschaft fest.
     */
    public void setMajor(int value) {
        this.major = value;
    }

    /**
     * Legt den Wert der minor-Eigenschaft fest.
     */
    public void setMinor(int value) {
        this.minor = value;
    }
}
