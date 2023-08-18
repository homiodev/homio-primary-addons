package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r ZoomLimits complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="ZoomLimits">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Range" type="{http://www.onvif.org/ver10/schema}Space1DDescription"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ZoomLimits",
        propOrder = {"range"})
public class ZoomLimits {

    @XmlElement(name = "Range", required = true)
    protected Space1DDescription range;

    /**
     * Ruft den Wert der range-Eigenschaft ab.
     *
     * @return possible object is {@link Space1DDescription }
     */
    public Space1DDescription getRange() {
        return range;
    }

    /**
     * Legt den Wert der range-Eigenschaft fest.
     *
     * @param value allowed object is {@link Space1DDescription }
     */
    public void setRange(Space1DDescription value) {
        this.range = value;
    }
}
