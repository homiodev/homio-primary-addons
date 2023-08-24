package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r OSDReference complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDReference">
 *   <simpleContent>
 *     <extension base="<http://www.onvif.org/ver10/schema>ReferenceToken">
 *       <anyAttribute processContents='lax'/>
 *     </extension>
 *   </simpleContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDReference",
        propOrder = {"value"})
public class OSDReference {

    /**
     * -- GETTER --
     *  Unique identifier for a physical or logical resource. Tokens should be assigned such that they are unique within a device. Tokens must be at least unique
     *  within its class. Length up to 64 characters.
     *
     * @return possible object is {@link String }
     */
    @XmlValue
    protected String value;
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
     * Legt den Wert der value-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

}
