package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r Space1DDescription complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Space1DDescription">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="URI" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         <element name="XRange" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Space1DDescription",
    propOrder = {"uri", "xRange"})
public class Space1DDescription {

    @XmlElement(name = "URI", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String uri;

    @XmlElement(name = "XRange", required = true)
    protected FloatRange xRange;

    /**
     * Ruft den Wert der uri-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getURI() {
        return uri;
    }

    /**
     * Legt den Wert der uri-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setURI(String value) {
        this.uri = value;
    }

    /**
     * Ruft den Wert der xRange-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    public FloatRange getXRange() {
        return xRange;
    }

    /**
     * Legt den Wert der xRange-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setXRange(FloatRange value) {
        this.xRange = value;
    }
}