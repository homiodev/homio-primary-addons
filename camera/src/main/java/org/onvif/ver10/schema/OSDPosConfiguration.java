package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r OSDPosConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDPosConfiguration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="Pos" type="{http://www.onvif.org/ver10/schema}Vector" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}OSDPosConfigurationExtension" minOccurs="0"/>
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
        name = "OSDPosConfiguration",
        propOrder = {"type", "pos", "extension"})
public class OSDPosConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Type", required = true)
    protected String type;

    /**
     * -- GETTER --
     *  Ruft den Wert der pos-Eigenschaft ab.
     *
     * @return possible object is {@link Vector }
     */
    @XmlElement(name = "Pos")
    protected Vector pos;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link OSDPosConfigurationExtension }
     */
    @XmlElement(name = "Extension")
    protected OSDPosConfigurationExtension extension;

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
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Legt den Wert der pos-Eigenschaft fest.
     *
     * @param value allowed object is {@link Vector }
     */
    public void setPos(Vector value) {
        this.pos = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDPosConfigurationExtension }
     */
    public void setExtension(OSDPosConfigurationExtension value) {
        this.extension = value;
    }

}
