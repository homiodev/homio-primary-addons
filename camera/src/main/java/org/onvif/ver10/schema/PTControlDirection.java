package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r PTControlDirection complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PTControlDirection">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="EFlip" type="{http://www.onvif.org/ver10/schema}EFlip" minOccurs="0"/>
 *         <element name="Reverse" type="{http://www.onvif.org/ver10/schema}Reverse" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}PTControlDirectionExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTControlDirection",
        propOrder = {"eFlip", "reverse", "extension"})
public class PTControlDirection {

    @XmlElement(name = "EFlip")
    protected EFlip eFlip;

    /**
     * -- GETTER --
     *  Ruft den Wert der reverse-Eigenschaft ab.
     *
     * @return possible object is {@link Reverse }
     */
    @Getter @XmlElement(name = "Reverse")
    protected Reverse reverse;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link PTControlDirectionExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected PTControlDirectionExtension extension;

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
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der eFlip-Eigenschaft ab.
     *
     * @return possible object is {@link EFlip }
     */
    public EFlip getEFlip() {
        return eFlip;
    }

    /**
     * Legt den Wert der eFlip-Eigenschaft fest.
     *
     * @param value allowed object is {@link EFlip }
     */
    public void setEFlip(EFlip value) {
        this.eFlip = value;
    }

    /**
     * Legt den Wert der reverse-Eigenschaft fest.
     *
     * @param value allowed object is {@link Reverse }
     */
    public void setReverse(Reverse value) {
        this.reverse = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTControlDirectionExtension }
     */
    public void setExtension(PTControlDirectionExtension value) {
        this.extension = value;
    }

}
