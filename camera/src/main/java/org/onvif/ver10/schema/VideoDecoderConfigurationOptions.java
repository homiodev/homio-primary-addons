package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r VideoDecoderConfigurationOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="VideoDecoderConfigurationOptions">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="JpegDecOptions" type="{http://www.onvif.org/ver10/schema}JpegDecOptions" minOccurs="0"/>
 *         <element name="H264DecOptions" type="{http://www.onvif.org/ver10/schema}H264DecOptions" minOccurs="0"/>
 *         <element name="Mpeg4DecOptions" type="{http://www.onvif.org/ver10/schema}Mpeg4DecOptions" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}VideoDecoderConfigurationOptionsExtension" minOccurs="0"/>
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
        name = "VideoDecoderConfigurationOptions",
        propOrder = {"jpegDecOptions", "h264DecOptions", "mpeg4DecOptions", "extension"})
public class VideoDecoderConfigurationOptions {

    /**
     * -- GETTER --
     *  Ruft den Wert der jpegDecOptions-Eigenschaft ab.
     *
     * @return possible object is {@link JpegDecOptions }
     */
    @XmlElement(name = "JpegDecOptions")
    protected JpegDecOptions jpegDecOptions;

    /**
     * -- GETTER --
     *  Ruft den Wert der h264DecOptions-Eigenschaft ab.
     *
     * @return possible object is {@link H264DecOptions }
     */
    @XmlElement(name = "H264DecOptions")
    protected H264DecOptions h264DecOptions;

    /**
     * -- GETTER --
     *  Ruft den Wert der mpeg4DecOptions-Eigenschaft ab.
     *
     * @return possible object is {@link Mpeg4DecOptions }
     */
    @XmlElement(name = "Mpeg4DecOptions")
    protected Mpeg4DecOptions mpeg4DecOptions;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link VideoDecoderConfigurationOptionsExtension }
     */
    @XmlElement(name = "Extension")
    protected VideoDecoderConfigurationOptionsExtension extension;

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
     * Legt den Wert der jpegDecOptions-Eigenschaft fest.
     *
     * @param value allowed object is {@link JpegDecOptions }
     */
    public void setJpegDecOptions(JpegDecOptions value) {
        this.jpegDecOptions = value;
    }

    /**
     * Legt den Wert der h264DecOptions-Eigenschaft fest.
     *
     * @param value allowed object is {@link H264DecOptions }
     */
    public void setH264DecOptions(H264DecOptions value) {
        this.h264DecOptions = value;
    }

    /**
     * Legt den Wert der mpeg4DecOptions-Eigenschaft fest.
     *
     * @param value allowed object is {@link Mpeg4DecOptions }
     */
    public void setMpeg4DecOptions(Mpeg4DecOptions value) {
        this.mpeg4DecOptions = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoDecoderConfigurationOptionsExtension }
     */
    public void setExtension(VideoDecoderConfigurationOptionsExtension value) {
        this.extension = value;
    }

}
