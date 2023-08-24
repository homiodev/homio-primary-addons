package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r RelativeFocusOptions20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="RelativeFocusOptions20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Distance" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *         <element name="Speed" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RelativeFocusOptions20",
        propOrder = {"distance", "speed"})
public class RelativeFocusOptions20 {

    /**
     * -- GETTER --
     *  Ruft den Wert der distance-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @XmlElement(name = "Distance", required = true)
    protected FloatRange distance;

    /**
     * -- GETTER --
     *  Ruft den Wert der speed-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @XmlElement(name = "Speed")
    protected FloatRange speed;

    /**
     * Legt den Wert der distance-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setDistance(FloatRange value) {
        this.distance = value;
    }

    /**
     * Legt den Wert der speed-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setSpeed(FloatRange value) {
        this.speed = value;
    }
}
