package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Representation of a physical video outputs.
 *
 * <p>Java-Klasse f�r VideoOutput complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="VideoOutput">
 *   <complexContent>
 *     <extension base="{http://www.onvif.org/ver10/schema}DeviceEntity">
 *       <sequence>
 *         <element name="Layout" type="{http://www.onvif.org/ver10/schema}Layout"/>
 *         <element name="Resolution" type="{http://www.onvif.org/ver10/schema}VideoResolution" minOccurs="0"/>
 *         <element name="RefreshRate" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="AspectRatio" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}VideoOutputExtension" minOccurs="0"/>
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
        name = "VideoOutput",
        propOrder = {"layout", "resolution", "refreshRate", "aspectRatio", "extension"})
public class VideoOutput extends DeviceEntity {

    /**
     * -- GETTER --
     *  Ruft den Wert der layout-Eigenschaft ab.
     *
     * @return possible object is {@link Layout }
     */
    @XmlElement(name = "Layout", required = true)
    protected Layout layout;

    /**
     * -- GETTER --
     *  Ruft den Wert der resolution-Eigenschaft ab.
     *
     * @return possible object is {@link VideoResolution }
     */
    @XmlElement(name = "Resolution")
    protected VideoResolution resolution;

    /**
     * -- GETTER --
     *  Ruft den Wert der refreshRate-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "RefreshRate")
    protected Float refreshRate;

    /**
     * -- GETTER --
     *  Ruft den Wert der aspectRatio-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "AspectRatio")
    protected Float aspectRatio;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link VideoOutputExtension }
     */
    @XmlElement(name = "Extension")
    protected VideoOutputExtension extension;

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
     * Legt den Wert der layout-Eigenschaft fest.
     *
     * @param value allowed object is {@link Layout }
     */
    public void setLayout(Layout value) {
        this.layout = value;
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
     * Legt den Wert der refreshRate-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setRefreshRate(Float value) {
        this.refreshRate = value;
    }

    /**
     * Legt den Wert der aspectRatio-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setAspectRatio(Float value) {
        this.aspectRatio = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoOutputExtension }
     */
    public void setExtension(VideoOutputExtension value) {
        this.extension = value;
    }

}
