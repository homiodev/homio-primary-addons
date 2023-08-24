package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r Rename complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Rename">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="from" type="{http://www.onvif.org/ver10/schema}ObjectId"/>
 *         <element name="to" type="{http://www.onvif.org/ver10/schema}ObjectId"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Rename",
        propOrder = {"from", "to"})
public class Rename {

    /**
     * -- GETTER --
     *  Ruft den Wert der from-Eigenschaft ab.
     *
     * @return possible object is {@link ObjectId }
     */
    @XmlElement(required = true)
    protected ObjectId from;

    /**
     * -- GETTER --
     *  Ruft den Wert der to-Eigenschaft ab.
     *
     * @return possible object is {@link ObjectId }
     */
    @XmlElement(required = true)
    protected ObjectId to;

    /**
     * Legt den Wert der from-Eigenschaft fest.
     *
     * @param value allowed object is {@link ObjectId }
     */
    public void setFrom(ObjectId value) {
        this.from = value;
    }

    /**
     * Legt den Wert der to-Eigenschaft fest.
     *
     * @param value allowed object is {@link ObjectId }
     */
    public void setTo(ObjectId value) {
        this.to = value;
    }
}
