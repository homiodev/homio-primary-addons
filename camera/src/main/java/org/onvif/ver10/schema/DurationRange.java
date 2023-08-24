package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import javax.xml.datatype.Duration;
import lombok.Getter;

/**
 * Range of duration greater equal Min duration and less equal Max duration.
 *
 * <p>Java-Klasse f�r DurationRange complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="DurationRange">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Min" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         <element name="Max" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "DurationRange",
        propOrder = {"min", "max"})
public class DurationRange {

    /**
     * -- GETTER --
     *  Ruft den Wert der min-Eigenschaft ab.
     *
     * @return possible object is {@link Duration }
     */
    @XmlElement(name = "Min", required = true)
    protected Duration min;

    /**
     * -- GETTER --
     *  Ruft den Wert der max-Eigenschaft ab.
     *
     * @return possible object is {@link Duration }
     */
    @XmlElement(name = "Max", required = true)
    protected Duration max;

    /**
     * Legt den Wert der min-Eigenschaft fest.
     *
     * @param value allowed object is {@link Duration }
     */
    public void setMin(Duration value) {
        this.min = value;
    }

    /**
     * Legt den Wert der max-Eigenschaft fest.
     *
     * @param value allowed object is {@link Duration }
     */
    public void setMax(Duration value) {
        this.max = value;
    }
}
