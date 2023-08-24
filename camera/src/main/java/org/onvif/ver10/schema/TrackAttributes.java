package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r TrackAttributes complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="TrackAttributes">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="TrackInformation" type="{http://www.onvif.org/ver10/schema}TrackInformation"/>
 *         <element name="VideoAttributes" type="{http://www.onvif.org/ver10/schema}VideoAttributes" minOccurs="0"/>
 *         <element name="AudioAttributes" type="{http://www.onvif.org/ver10/schema}AudioAttributes" minOccurs="0"/>
 *         <element name="MetadataAttributes" type="{http://www.onvif.org/ver10/schema}MetadataAttributes" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}TrackAttributesExtension" minOccurs="0"/>
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
        name = "TrackAttributes",
        propOrder = {
                "trackInformation",
                "videoAttributes",
                "audioAttributes",
                "metadataAttributes",
                "extension"
        })
public class TrackAttributes {

    /**
     * -- GETTER --
     *  Ruft den Wert der trackInformation-Eigenschaft ab.
     *
     * @return possible object is {@link TrackInformation }
     */
    @XmlElement(name = "TrackInformation", required = true)
    protected TrackInformation trackInformation;

    /**
     * -- GETTER --
     *  Ruft den Wert der videoAttributes-Eigenschaft ab.
     *
     * @return possible object is {@link VideoAttributes }
     */
    @XmlElement(name = "VideoAttributes")
    protected VideoAttributes videoAttributes;

    /**
     * -- GETTER --
     *  Ruft den Wert der audioAttributes-Eigenschaft ab.
     *
     * @return possible object is {@link AudioAttributes }
     */
    @XmlElement(name = "AudioAttributes")
    protected AudioAttributes audioAttributes;

    /**
     * -- GETTER --
     *  Ruft den Wert der metadataAttributes-Eigenschaft ab.
     *
     * @return possible object is {@link MetadataAttributes }
     */
    @XmlElement(name = "MetadataAttributes")
    protected MetadataAttributes metadataAttributes;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link TrackAttributesExtension }
     */
    @XmlElement(name = "Extension")
    protected TrackAttributesExtension extension;

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
     * Legt den Wert der trackInformation-Eigenschaft fest.
     *
     * @param value allowed object is {@link TrackInformation }
     */
    public void setTrackInformation(TrackInformation value) {
        this.trackInformation = value;
    }

    /**
     * Legt den Wert der videoAttributes-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoAttributes }
     */
    public void setVideoAttributes(VideoAttributes value) {
        this.videoAttributes = value;
    }

    /**
     * Legt den Wert der audioAttributes-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioAttributes }
     */
    public void setAudioAttributes(AudioAttributes value) {
        this.audioAttributes = value;
    }

    /**
     * Legt den Wert der metadataAttributes-Eigenschaft fest.
     *
     * @param value allowed object is {@link MetadataAttributes }
     */
    public void setMetadataAttributes(MetadataAttributes value) {
        this.metadataAttributes = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link TrackAttributesExtension }
     */
    public void setExtension(TrackAttributesExtension value) {
        this.extension = value;
    }

}
