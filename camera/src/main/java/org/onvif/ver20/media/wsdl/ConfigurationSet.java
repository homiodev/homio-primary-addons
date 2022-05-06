package org.onvif.ver20.media.wsdl;

import org.onvif.ver10.schema.*;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigurationSet", propOrder = {
    "videoSource",
    "audioSource",
    "videoEncoder",
    "audioEncoder",
    "analytics",
    "ptz",
    "metadata",
    "audioOutput",
    "audioDecoder",
    "any"
})
public class ConfigurationSet {

    @XmlElement(name = "VideoSource")
    protected VideoSourceConfiguration videoSource;
    @XmlElement(name = "AudioSource")
    protected AudioSourceConfiguration audioSource;
    @XmlElement(name = "VideoEncoder")
    protected VideoEncoder2Configuration videoEncoder;
    @XmlElement(name = "AudioEncoder")
    protected AudioEncoder2Configuration audioEncoder;
    @XmlElement(name = "Analytics")
    protected ConfigurationEntity analytics;
    @XmlElement(name = "PTZ")
    protected PTZConfiguration ptz;
    @XmlElement(name = "Metadata")
    protected MetadataConfiguration metadata;
    @XmlElement(name = "AudioOutput")
    protected AudioOutputConfiguration audioOutput;
    @XmlElement(name = "AudioDecoder")
    protected AudioDecoderConfiguration audioDecoder;
    @XmlAnyElement
    protected List<Element> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der videoSource-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link VideoSourceConfiguration }
     *
     */
    public VideoSourceConfiguration getVideoSource() {
        return videoSource;
    }

    /**
     * Legt den Wert der videoSource-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link VideoSourceConfiguration }
     *
     */
    public void setVideoSource(VideoSourceConfiguration value) {
        this.videoSource = value;
    }

    /**
     * Ruft den Wert der audioSource-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link AudioSourceConfiguration }
     *
     */
    public AudioSourceConfiguration getAudioSource() {
        return audioSource;
    }

    /**
     * Legt den Wert der audioSource-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link AudioSourceConfiguration }
     *
     */
    public void setAudioSource(AudioSourceConfiguration value) {
        this.audioSource = value;
    }

    /**
     * Ruft den Wert der videoEncoder-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link VideoEncoder2Configuration }
     *
     */
    public VideoEncoder2Configuration getVideoEncoder() {
        return videoEncoder;
    }

    /**
     * Legt den Wert der videoEncoder-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link VideoEncoder2Configuration }
     *
     */
    public void setVideoEncoder(VideoEncoder2Configuration value) {
        this.videoEncoder = value;
    }

    /**
     * Ruft den Wert der audioEncoder-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link AudioEncoder2Configuration }
     *
     */
    public AudioEncoder2Configuration getAudioEncoder() {
        return audioEncoder;
    }

    /**
     * Legt den Wert der audioEncoder-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link AudioEncoder2Configuration }
     *
     */
    public void setAudioEncoder(AudioEncoder2Configuration value) {
        this.audioEncoder = value;
    }

    /**
     * Ruft den Wert der analytics-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ConfigurationEntity }
     *
     */
    public ConfigurationEntity getAnalytics() {
        return analytics;
    }

    /**
     * Legt den Wert der analytics-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ConfigurationEntity }
     *
     */
    public void setAnalytics(ConfigurationEntity value) {
        this.analytics = value;
    }

    /**
     * Ruft den Wert der ptz-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link PTZConfiguration }
     *
     */
    public PTZConfiguration getPTZ() {
        return ptz;
    }

    /**
     * Legt den Wert der ptz-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link PTZConfiguration }
     *
     */
    public void setPTZ(PTZConfiguration value) {
        this.ptz = value;
    }

    /**
     * Ruft den Wert der metadata-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link MetadataConfiguration }
     *
     */
    public MetadataConfiguration getMetadata() {
        return metadata;
    }

    /**
     * Legt den Wert der metadata-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link MetadataConfiguration }
     *
     */
    public void setMetadata(MetadataConfiguration value) {
        this.metadata = value;
    }

    /**
     * Ruft den Wert der audioOutput-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link AudioOutputConfiguration }
     *
     */
    public AudioOutputConfiguration getAudioOutput() {
        return audioOutput;
    }

    /**
     * Legt den Wert der audioOutput-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link AudioOutputConfiguration }
     *
     */
    public void setAudioOutput(AudioOutputConfiguration value) {
        this.audioOutput = value;
    }

    /**
     * Ruft den Wert der audioDecoder-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link AudioDecoderConfiguration }
     *
     */
    public AudioDecoderConfiguration getAudioDecoder() {
        return audioDecoder;
    }

    /**
     * Legt den Wert der audioDecoder-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link AudioDecoderConfiguration }
     *
     */
    public void setAudioDecoder(AudioDecoderConfiguration value) {
        this.audioDecoder = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     *
     *
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<Element>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
