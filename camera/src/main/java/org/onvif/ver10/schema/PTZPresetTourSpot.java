package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r PTZPresetTourSpot complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PTZPresetTourSpot">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PresetDetail" type="{http://www.onvif.org/ver10/schema}PTZPresetTourPresetDetail"/>
 *         <element name="Speed" type="{http://www.onvif.org/ver10/schema}PTZSpeed" minOccurs="0"/>
 *         <element name="StayTime" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}PTZPresetTourSpotExtension" minOccurs="0"/>
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
        name = "PTZPresetTourSpot",
        propOrder = {"presetDetail", "speed", "stayTime", "extension"})
public class PTZPresetTourSpot {

    /**
     * -- GETTER --
     *  Ruft den Wert der presetDetail-Eigenschaft ab.
     *
     * @return possible object is {@link PTZPresetTourPresetDetail }
     */
    @XmlElement(name = "PresetDetail", required = true)
    protected PTZPresetTourPresetDetail presetDetail;

    /**
     * -- GETTER --
     *  Ruft den Wert der speed-Eigenschaft ab.
     *
     * @return possible object is {@link PTZSpeed }
     */
    @XmlElement(name = "Speed")
    protected PTZSpeed speed;

    /**
     * -- GETTER --
     *  Ruft den Wert der stayTime-Eigenschaft ab.
     *
     * @return possible object is {@link Duration }
     */
    @XmlElement(name = "StayTime")
    protected Duration stayTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link PTZPresetTourSpotExtension }
     */
    @XmlElement(name = "Extension")
    protected PTZPresetTourSpotExtension extension;

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
     * Legt den Wert der presetDetail-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZPresetTourPresetDetail }
     */
    public void setPresetDetail(PTZPresetTourPresetDetail value) {
        this.presetDetail = value;
    }

    /**
     * Legt den Wert der speed-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZSpeed }
     */
    public void setSpeed(PTZSpeed value) {
        this.speed = value;
    }

    /**
     * Legt den Wert der stayTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link Duration }
     */
    public void setStayTime(Duration value) {
        this.stayTime = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZPresetTourSpotExtension }
     */
    public void setExtension(PTZPresetTourSpotExtension value) {
        this.extension = value;
    }

}
