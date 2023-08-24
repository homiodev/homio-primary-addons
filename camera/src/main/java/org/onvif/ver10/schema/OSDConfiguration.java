package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r OSDConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDConfiguration">
 *   <complexContent>
 *     <extension base="{http://www.onvif.org/ver10/schema}DeviceEntity">
 *       <sequence>
 *         <element name="VideoSourceConfigurationToken" type="{http://www.onvif.org/ver10/schema}OSDReference"/>
 *         <element name="Type" type="{http://www.onvif.org/ver10/schema}OSDType"/>
 *         <element name="Position" type="{http://www.onvif.org/ver10/schema}OSDPosConfiguration"/>
 *         <element name="TextString" type="{http://www.onvif.org/ver10/schema}OSDTextConfiguration" minOccurs="0"/>
 *         <element name="Image" type="{http://www.onvif.org/ver10/schema}OSDImgConfiguration" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}OSDConfigurationExtension" minOccurs="0"/>
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
        name = "OSDConfiguration",
        propOrder = {
                "videoSourceConfigurationToken",
                "type",
                "position",
                "textString",
                "image",
                "extension"
        })
public class OSDConfiguration extends DeviceEntity {

    /**
     * -- GETTER --
     *  Ruft den Wert der videoSourceConfigurationToken-Eigenschaft ab.
     *
     * @return possible object is {@link OSDReference }
     */
    @XmlElement(name = "VideoSourceConfigurationToken", required = true)
    protected OSDReference videoSourceConfigurationToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is {@link OSDType }
     */
    @XmlElement(name = "Type", required = true)
    protected OSDType type;

    /**
     * -- GETTER --
     *  Ruft den Wert der position-Eigenschaft ab.
     *
     * @return possible object is {@link OSDPosConfiguration }
     */
    @XmlElement(name = "Position", required = true)
    protected OSDPosConfiguration position;

    /**
     * -- GETTER --
     *  Ruft den Wert der textString-Eigenschaft ab.
     *
     * @return possible object is {@link OSDTextConfiguration }
     */
    @XmlElement(name = "TextString")
    protected OSDTextConfiguration textString;

    /**
     * -- GETTER --
     *  Ruft den Wert der image-Eigenschaft ab.
     *
     * @return possible object is {@link OSDImgConfiguration }
     */
    @XmlElement(name = "Image")
    protected OSDImgConfiguration image;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link OSDConfigurationExtension }
     */
    @XmlElement(name = "Extension")
    protected OSDConfigurationExtension extension;

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
     * Legt den Wert der videoSourceConfigurationToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDReference }
     */
    public void setVideoSourceConfigurationToken(OSDReference value) {
        this.videoSourceConfigurationToken = value;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDType }
     */
    public void setType(OSDType value) {
        this.type = value;
    }

    /**
     * Legt den Wert der position-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDPosConfiguration }
     */
    public void setPosition(OSDPosConfiguration value) {
        this.position = value;
    }

    /**
     * Legt den Wert der textString-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDTextConfiguration }
     */
    public void setTextString(OSDTextConfiguration value) {
        this.textString = value;
    }

    /**
     * Legt den Wert der image-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDImgConfiguration }
     */
    public void setImage(OSDImgConfiguration value) {
        this.image = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDConfigurationExtension }
     */
    public void setExtension(OSDConfigurationExtension value) {
        this.extension = value;
    }

}
