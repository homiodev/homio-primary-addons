package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r PTZPresetTourStatus complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PTZPresetTourStatus">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="State" type="{http://www.onvif.org/ver10/schema}PTZPresetTourState"/>
 *         <element name="CurrentTourSpot" type="{http://www.onvif.org/ver10/schema}PTZPresetTourSpot" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}PTZPresetTourStatusExtension" minOccurs="0"/>
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
        name = "PTZPresetTourStatus",
        propOrder = {"state", "currentTourSpot", "extension"})
public class PTZPresetTourStatus {

    /**
     * -- GETTER --
     *  Ruft den Wert der state-Eigenschaft ab.
     *
     * @return possible object is {@link PTZPresetTourState }
     */
    @XmlElement(name = "State", required = true)
    protected PTZPresetTourState state;

    /**
     * -- GETTER --
     *  Ruft den Wert der currentTourSpot-Eigenschaft ab.
     *
     * @return possible object is {@link PTZPresetTourSpot }
     */
    @XmlElement(name = "CurrentTourSpot")
    protected PTZPresetTourSpot currentTourSpot;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link PTZPresetTourStatusExtension }
     */
    @XmlElement(name = "Extension")
    protected PTZPresetTourStatusExtension extension;

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
     * Legt den Wert der state-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZPresetTourState }
     */
    public void setState(PTZPresetTourState value) {
        this.state = value;
    }

    /**
     * Legt den Wert der currentTourSpot-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZPresetTourSpot }
     */
    public void setCurrentTourSpot(PTZPresetTourSpot value) {
        this.currentTourSpot = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZPresetTourStatusExtension }
     */
    public void setExtension(PTZPresetTourStatusExtension value) {
        this.extension = value;
    }

}
