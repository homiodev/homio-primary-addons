package org.onvif.ver10.schema;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * Java-Klasse f�r LensOffset complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * &lt;complexType name="LensOffset">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LensOffset")
public class LensOffset {

    @XmlAttribute(name = "x")
    protected Float x;

    @XmlAttribute(name = "y")
    protected Float y;

    @XmlAnyAttribute private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der x-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    public Float getX() {
        return x;
    }

    /**
     * Legt den Wert der x-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setX(Float value) {
        this.x = value;
    }

    /**
     * Ruft den Wert der y-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    public Float getY() {
        return y;
    }

    /**
     * Legt den Wert der y-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setY(Float value) {
        this.y = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>the map is keyed by the name of the attribute and the value is the string value of the
     * attribute.
     *
     * <p>the map returned by this method is live, and you can add new attribute by updating the map
     * directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}