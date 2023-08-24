package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r Time complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Time">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Hour" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="Minute" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="Second" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Time",
        propOrder = {"hour", "minute", "second"})
public class Time {

    /**
     * -- GETTER --
     *  Ruft den Wert der hour-Eigenschaft ab.
     */
    @XmlElement(name = "Hour")
    protected int hour;

    /**
     * -- GETTER --
     *  Ruft den Wert der minute-Eigenschaft ab.
     */
    @XmlElement(name = "Minute")
    protected int minute;

    /**
     * -- GETTER --
     *  Ruft den Wert der second-Eigenschaft ab.
     */
    @XmlElement(name = "Second")
    protected int second;

    /**
     * Legt den Wert der hour-Eigenschaft fest.
     */
    public void setHour(int value) {
        this.hour = value;
    }

    /**
     * Legt den Wert der minute-Eigenschaft fest.
     */
    public void setMinute(int value) {
        this.minute = value;
    }

    /**
     * Legt den Wert der second-Eigenschaft fest.
     */
    public void setSecond(int value) {
        this.second = value;
    }
}
