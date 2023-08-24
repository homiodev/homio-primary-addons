package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r Object complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Object">
 *   <complexContent>
 *     <extension base="{http://www.onvif.org/ver10/schema}ObjectId">
 *       <sequence>
 *         <element name="Appearance" type="{http://www.onvif.org/ver10/schema}Appearance" minOccurs="0"/>
 *         <element name="Behaviour" type="{http://www.onvif.org/ver10/schema}Behaviour" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}ObjectExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Object",
        propOrder = {"appearance", "behaviour", "extension"})
public class Object extends ObjectId {

    /**
     * -- GETTER --
     *  Ruft den Wert der appearance-Eigenschaft ab.
     *
     * @return possible object is {@link Appearance }
     */
    @XmlElement(name = "Appearance")
    protected Appearance appearance;

    /**
     * -- GETTER --
     *  Ruft den Wert der behaviour-Eigenschaft ab.
     *
     * @return possible object is {@link Behaviour }
     */
    @XmlElement(name = "Behaviour")
    protected Behaviour behaviour;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ObjectExtension }
     */
    @XmlElement(name = "Extension")
    protected ObjectExtension extension;

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
     * Legt den Wert der appearance-Eigenschaft fest.
     *
     * @param value allowed object is {@link Appearance }
     */
    public void setAppearance(Appearance value) {
        this.appearance = value;
    }

    /**
     * Legt den Wert der behaviour-Eigenschaft fest.
     *
     * @param value allowed object is {@link Behaviour }
     */
    public void setBehaviour(Behaviour value) {
        this.behaviour = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ObjectExtension }
     */
    public void setExtension(ObjectExtension value) {
        this.extension = value;
    }

}
