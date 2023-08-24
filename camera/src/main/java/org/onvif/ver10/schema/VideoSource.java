package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Representation of a physical video input.
 *
 * <p>Java-Klasse f�r VideoSource complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="VideoSource">
 *   <complexContent>
 *     <extension base="{http://www.onvif.org/ver10/schema}DeviceEntity">
 *       <sequence>
 *         <element name="Framerate" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="Resolution" type="{http://www.onvif.org/ver10/schema}VideoResolution"/>
 *         <element name="Imaging" type="{http://www.onvif.org/ver10/schema}ImagingSettings" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}VideoSourceExtension" minOccurs="0"/>
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
        name = "VideoSource",
        propOrder = {"framerate", "resolution", "imaging", "extension"})
public class VideoSource extends DeviceEntity {

    /**
     * -- GETTER --
     *  Ruft den Wert der framerate-Eigenschaft ab.
     */
    @XmlElement(name = "Framerate")
    protected float framerate;

    /**
     * -- GETTER --
     *  Ruft den Wert der resolution-Eigenschaft ab.
     *
     * @return possible object is {@link VideoResolution }
     */
    @XmlElement(name = "Resolution", required = true)
    protected VideoResolution resolution;

    /**
     * -- GETTER --
     *  Ruft den Wert der imaging-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingSettings }
     */
    @XmlElement(name = "Imaging")
    protected ImagingSettings imaging;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link VideoSourceExtension }
     */
    @XmlElement(name = "Extension")
    protected VideoSourceExtension extension;

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
     * Legt den Wert der framerate-Eigenschaft fest.
     */
    public void setFramerate(float value) {
        this.framerate = value;
    }

    /**
     * Legt den Wert der resolution-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoResolution }
     */
    public void setResolution(VideoResolution value) {
        this.resolution = value;
    }

    /**
     * Legt den Wert der imaging-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingSettings }
     */
    public void setImaging(ImagingSettings value) {
        this.imaging = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoSourceExtension }
     */
    public void setExtension(VideoSourceExtension value) {
        this.extension = value;
    }

}
