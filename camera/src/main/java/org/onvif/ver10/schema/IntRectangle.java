package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Rectangle defined by lower left corner position and size. Units are pixel.
 *
 * <p>Java-Klasse f�r IntRectangle complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="IntRectangle">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="width" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="height" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntRectangle")
public class IntRectangle {

    /**
     * -- GETTER --
     *  Ruft den Wert der x-Eigenschaft ab.
     */
    @XmlAttribute(name = "x", required = true)
    protected int x;

    /**
     * -- GETTER --
     *  Ruft den Wert der y-Eigenschaft ab.
     */
    @XmlAttribute(name = "y", required = true)
    protected int y;

    /**
     * -- GETTER --
     *  Ruft den Wert der width-Eigenschaft ab.
     */
    @XmlAttribute(name = "width", required = true)
    protected int width;

    /**
     * -- GETTER --
     *  Ruft den Wert der height-Eigenschaft ab.
     */
    @XmlAttribute(name = "height", required = true)
    protected int height;

    /**
     * Legt den Wert der x-Eigenschaft fest.
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * Legt den Wert der y-Eigenschaft fest.
     */
    public void setY(int value) {
        this.y = value;
    }

    /**
     * Legt den Wert der width-Eigenschaft fest.
     */
    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Legt den Wert der height-Eigenschaft fest.
     */
    public void setHeight(int value) {
        this.height = value;
    }
}
