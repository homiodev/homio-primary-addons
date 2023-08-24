package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Range of values greater equal Min value and less equal Max value.
 *
 * <p>Java-Klasse f�r FloatRange complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="FloatRange">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Min" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="Max" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FloatRange",
        propOrder = {"min", "max"})
public class FloatRange {

    /**
     * -- GETTER --
     *  Ruft den Wert der min-Eigenschaft ab.
     */
    @XmlElement(name = "Min")
    protected float min;

    /**
     * -- GETTER --
     *  Ruft den Wert der max-Eigenschaft ab.
     */
    @XmlElement(name = "Max")
    protected float max;

    /**
     * Legt den Wert der min-Eigenschaft fest.
     */
    public void setMin(float value) {
        this.min = value;
    }

    /**
     * Legt den Wert der max-Eigenschaft fest.
     */
    public void setMax(float value) {
        this.max = value;
    }
}
