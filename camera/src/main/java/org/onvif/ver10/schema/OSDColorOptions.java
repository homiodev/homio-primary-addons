package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Describe the option of the color and its transparency.
 *
 * <p>Java-Klasse f�r OSDColorOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDColorOptions">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Color" type="{http://www.onvif.org/ver10/schema}ColorOptions" minOccurs="0"/>
 *         <element name="Transparent" type="{http://www.onvif.org/ver10/schema}IntRange" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}OSDColorOptionsExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDColorOptions",
        propOrder = {"color", "transparent", "extension"})
public class OSDColorOptions {

    /**
     * -- GETTER --
     *  Ruft den Wert der color-Eigenschaft ab.
     *
     * @return possible object is {@link ColorOptions }
     */
    @XmlElement(name = "Color")
    protected ColorOptions color;

    /**
     * -- GETTER --
     *  Ruft den Wert der transparent-Eigenschaft ab.
     *
     * @return possible object is {@link IntRange }
     */
    @XmlElement(name = "Transparent")
    protected IntRange transparent;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link OSDColorOptionsExtension }
     */
    @XmlElement(name = "Extension")
    protected OSDColorOptionsExtension extension;

    /**
     * -- GETTER --
     *  Gets a map that contains attributes that aren't bound to any typed property on this class.
     *  <p>the map is keyed by the name of the attribute and the value is the string value of the
     *  attribute.
     *  <p>the map returned by this method is live, and you can add new attribute by updating the map
     *  directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Legt den Wert der color-Eigenschaft fest.
     *
     * @param value allowed object is {@link ColorOptions }
     */
    public void setColor(ColorOptions value) {
        this.color = value;
    }

    /**
     * Legt den Wert der transparent-Eigenschaft fest.
     *
     * @param value allowed object is {@link IntRange }
     */
    public void setTransparent(IntRange value) {
        this.transparent = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDColorOptionsExtension }
     */
    public void setExtension(OSDColorOptionsExtension value) {
        this.extension = value;
    }

}
