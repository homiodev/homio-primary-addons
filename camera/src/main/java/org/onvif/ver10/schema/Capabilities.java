package org.onvif.ver10.schema;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Capabilities",
    propOrder = {"analytics", "device", "events", "imaging", "media", "ptz", "extension"})
public class Capabilities {

    @XmlElement(name = "Analytics")
    protected AnalyticsCapabilities analytics;

    @XmlElement(name = "Device")
    protected DeviceCapabilities device;

    @XmlElement(name = "Events")
    protected EventCapabilities events;

    @XmlElement(name = "Imaging")
    protected ImagingCapabilities imaging;

    @XmlElement(name = "Media")
    protected MediaCapabilities media;

    @XmlElement(name = "PTZ")
    protected PTZCapabilities ptz;

    @XmlElement(name = "Extension")
    protected CapabilitiesExtension extension;

    @XmlAnyAttribute private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der analytics-Eigenschaft ab.
     *
     * @return possible object is {@link AnalyticsCapabilities }
     */
    public AnalyticsCapabilities getAnalytics() {
        return analytics;
    }

    /**
     * Legt den Wert der analytics-Eigenschaft fest.
     *
     * @param value allowed object is {@link AnalyticsCapabilities }
     */
    public void setAnalytics(AnalyticsCapabilities value) {
        this.analytics = value;
    }

    /**
     * Ruft den Wert der device-Eigenschaft ab.
     *
     * @return possible object is {@link DeviceCapabilities }
     */
    public DeviceCapabilities getDevice() {
        return device;
    }

    /**
     * Legt den Wert der device-Eigenschaft fest.
     *
     * @param value allowed object is {@link DeviceCapabilities }
     */
    public void setDevice(DeviceCapabilities value) {
        this.device = value;
    }

    /**
     * Ruft den Wert der events-Eigenschaft ab.
     *
     * @return possible object is {@link EventCapabilities }
     */
    public EventCapabilities getEvents() {
        return events;
    }

    /**
     * Legt den Wert der events-Eigenschaft fest.
     *
     * @param value allowed object is {@link EventCapabilities }
     */
    public void setEvents(EventCapabilities value) {
        this.events = value;
    }

    /**
     * Ruft den Wert der imaging-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingCapabilities }
     */
    public ImagingCapabilities getImaging() {
        return imaging;
    }

    /**
     * Legt den Wert der imaging-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingCapabilities }
     */
    public void setImaging(ImagingCapabilities value) {
        this.imaging = value;
    }

    /**
     * Ruft den Wert der media-Eigenschaft ab.
     *
     * @return possible object is {@link MediaCapabilities }
     */
    public MediaCapabilities getMedia() {
        return media;
    }

    /**
     * Legt den Wert der media-Eigenschaft fest.
     *
     * @param value allowed object is {@link MediaCapabilities }
     */
    public void setMedia(MediaCapabilities value) {
        this.media = value;
    }

    /**
     * Ruft den Wert der ptz-Eigenschaft ab.
     *
     * @return possible object is {@link PTZCapabilities }
     */
    public PTZCapabilities getPTZ() {
        return ptz;
    }

    /**
     * Legt den Wert der ptz-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZCapabilities }
     */
    public void setPTZ(PTZCapabilities value) {
        this.ptz = value;
    }

    /**
     * Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link CapabilitiesExtension }
     */
    public CapabilitiesExtension getExtension() {
        return extension;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link CapabilitiesExtension }
     */
    public void setExtension(CapabilitiesExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>the map is keyed by the name of the attribute and the value is the string value of the
     * attribute.
     *
     * <p>the map returned by this method is live, and you can add new attribute by updating the map
     * directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}