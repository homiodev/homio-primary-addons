package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r PTZFilter complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PTZFilter">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Status" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="Position" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        name = "PTZFilter",
        propOrder = {"status", "position"})
public class PTZFilter {

    /**
     * -- GETTER --
     *  Ruft den Wert der status-Eigenschaft ab.
     */
    @XmlElement(name = "Status")
    protected boolean status;

    /**
     * -- GETTER --
     *  Ruft den Wert der position-Eigenschaft ab.
     */
    @XmlElement(name = "Position")
    protected boolean position;

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
     * Legt den Wert der status-Eigenschaft fest.
     */
    public void setStatus(boolean value) {
        this.status = value;
    }

    /**
     * Legt den Wert der position-Eigenschaft fest.
     */
    public void setPosition(boolean value) {
        this.position = value;
    }

}
