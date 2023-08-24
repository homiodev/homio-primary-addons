package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

/**
 * Java-Klasse f�r Vector1D complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Vector1D">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       <attribute name="space" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Vector1D")
public class Vector1D {

    /**
     * -- GETTER --
     *  Ruft den Wert der x-Eigenschaft ab.
     */
    @XmlAttribute(name = "x", required = true)
    protected float x;

    /**
     * -- GETTER --
     *  Ruft den Wert der space-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "space")
    @XmlSchemaType(name = "anyURI")
    protected String space;

    /**
     * Legt den Wert der x-Eigenschaft fest.
     */
    public void setX(float value) {
        this.x = value;
    }

    /**
     * Legt den Wert der space-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setSpace(String value) {
        this.space = value;
    }
}
