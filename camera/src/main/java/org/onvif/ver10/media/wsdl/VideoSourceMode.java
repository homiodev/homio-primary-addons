//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.19 um 02:35:56 PM CET
//

package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.VideoResolution;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java-Klasse f�r VideoSourceMode complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="VideoSourceMode">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="MaxFramerate" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="MaxResolution" type="{http://www.onvif.org/ver10/schema}VideoResolution"/>
 *         <element name="Encodings" type="{http://www.onvif.org/ver10/media/wsdl}EncodingTypes"/>
 *         <element name="Reboot" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="Description" type="{http://www.onvif.org/ver10/schema}Description" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/media/wsdl}VideoSourceModeExtension" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="token" use="required" type="{http://www.onvif.org/ver10/schema}ReferenceToken" />
 *       <attribute name="Enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoSourceMode",
        propOrder = {
                "maxFramerate",
                "maxResolution",
                "encodings",
                "reboot",
                "description",
                "extension"
        })
public class VideoSourceMode {

    /**
     * -- GETTER --
     *  Ruft den Wert der maxFramerate-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "MaxFramerate")
    protected float maxFramerate;

    /**
     * -- GETTER --
     *  Ruft den Wert der maxResolution-Eigenschaft ab.
     *
     * @return possible object is {@link VideoResolution }
     */
    @Getter @XmlElement(name = "MaxResolution", required = true)
    protected VideoResolution maxResolution;

    @XmlList
    @XmlElement(name = "Encodings", required = true)
    protected List<String> encodings;

    /**
     * -- GETTER --
     *  Ruft den Wert der reboot-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "Reboot")
    protected boolean reboot;

    /**
     * -- GETTER --
     *  Ruft den Wert der description-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "Description")
    protected String description;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link VideoSourceModeExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected VideoSourceModeExtension extension;

    /**
     * -- GETTER --
     *  Ruft den Wert der token-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlAttribute(name = "token", required = true)
    protected String token;

    @XmlAttribute(name = "Enabled")
    protected Boolean enabled;

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
     * Legt den Wert der maxFramerate-Eigenschaft fest.
     */
    public void setMaxFramerate(float value) {
        this.maxFramerate = value;
    }

    /**
     * Legt den Wert der maxResolution-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoResolution }
     */
    public void setMaxResolution(VideoResolution value) {
        this.maxResolution = value;
    }

    /**
     * Gets the value of the encodings property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the encodings
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getEncodings().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getEncodings() {
        if (encodings == null) {
            encodings = new ArrayList<String>();
        }
        return this.encodings;
    }

    /**
     * Legt den Wert der reboot-Eigenschaft fest.
     */
    public void setReboot(boolean value) {
        this.reboot = value;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoSourceModeExtension }
     */
    public void setExtension(VideoSourceModeExtension value) {
        this.extension = value;
    }

    /**
     * Legt den Wert der token-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Ruft den Wert der enabled-Eigenschaft ab.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * Legt den Wert der enabled-Eigenschaft fest.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

}
