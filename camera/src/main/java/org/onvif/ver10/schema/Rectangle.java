package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r Rectangle complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Rectangle">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="bottom" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       <attribute name="top" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       <attribute name="right" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       <attribute name="left" type="{http://www.w3.org/2001/XMLSchema}float" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rectangle")
public class Rectangle {

    /**
     * -- GETTER --
     *  Ruft den Wert der bottom-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlAttribute(name = "bottom")
    protected Float bottom;

    /**
     * -- GETTER --
     *  Ruft den Wert der top-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlAttribute(name = "top")
    protected Float top;

    /**
     * -- GETTER --
     *  Ruft den Wert der right-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlAttribute(name = "right")
    protected Float right;

    /**
     * -- GETTER --
     *  Ruft den Wert der left-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlAttribute(name = "left")
    protected Float left;

    /**
     * Legt den Wert der bottom-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setBottom(Float value) {
        this.bottom = value;
    }

    /**
     * Legt den Wert der top-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setTop(Float value) {
        this.top = value;
    }

    /**
     * Legt den Wert der right-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setRight(Float value) {
        this.right = value;
    }

    /**
     * Legt den Wert der left-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setLeft(Float value) {
        this.left = value;
    }
}
