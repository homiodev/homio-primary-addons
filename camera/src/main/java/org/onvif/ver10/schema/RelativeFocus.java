package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r RelativeFocus complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="RelativeFocus">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Distance" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="Speed" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RelativeFocus",
        propOrder = {"distance", "speed"})
public class RelativeFocus {

    /**
     * -- GETTER --
     *  Ruft den Wert der distance-Eigenschaft ab.
     */
    @XmlElement(name = "Distance")
    protected float distance;

    /**
     * -- GETTER --
     *  Ruft den Wert der speed-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Speed")
    protected Float speed;

    /**
     * Legt den Wert der distance-Eigenschaft fest.
     */
    public void setDistance(float value) {
        this.distance = value;
    }

    /**
     * Legt den Wert der speed-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setSpeed(Float value) {
        this.speed = value;
    }
}
