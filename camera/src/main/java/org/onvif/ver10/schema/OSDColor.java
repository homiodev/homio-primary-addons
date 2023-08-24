package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * The value range of "Transparent" could be defined by vendors only should follow this rule: the minimum value means non-transparent and the maximum value
 * maens fully transparent.
 *
 * <p>Java-Klasse f�r OSDColor complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDColor">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Color" type="{http://www.onvif.org/ver10/schema}Color"/>
 *       </sequence>
 *       <attribute name="Transparent" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDColor",
        propOrder = {"color"})
public class OSDColor {

    /**
     * -- GETTER --
     *  Ruft den Wert der color-Eigenschaft ab.
     *
     * @return possible object is {@link Color }
     */
    @XmlElement(name = "Color", required = true)
    protected Color color;

    /**
     * -- GETTER --
     *  Ruft den Wert der transparent-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @XmlAttribute(name = "Transparent")
    protected Integer transparent;

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
     * @param value allowed object is {@link Color }
     */
    public void setColor(Color value) {
        this.color = value;
    }

    /**
     * Legt den Wert der transparent-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setTransparent(Integer value) {
        this.transparent = value;
    }

}
