package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Capabilities",
        propOrder = {"analytics", "device", "events", "imaging", "media", "ptz", "extension"})
public class Capabilities {

    
    @Getter @XmlElement(name = "Analytics")
    protected AnalyticsCapabilities analytics;

    
    @Getter @XmlElement(name = "Device")
    protected DeviceCapabilities device;

    
    @Getter @XmlElement(name = "Events")
    protected EventCapabilities events;

    
    @Getter @XmlElement(name = "Imaging")
    protected ImagingCapabilities imaging;

    
    @Getter @XmlElement(name = "Media")
    protected MediaCapabilities media;

    @XmlElement(name = "PTZ")
    protected PTZCapabilities ptz;

    
    @Getter @XmlElement(name = "Extension")
    protected CapabilitiesExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setAnalytics(AnalyticsCapabilities value) {
        this.analytics = value;
    }

    
    public void setDevice(DeviceCapabilities value) {
        this.device = value;
    }

    
    public void setEvents(EventCapabilities value) {
        this.events = value;
    }

    
    public void setImaging(ImagingCapabilities value) {
        this.imaging = value;
    }

    
    public void setMedia(MediaCapabilities value) {
        this.media = value;
    }

    
    public PTZCapabilities getPTZ() {
        return ptz;
    }

    
    public void setPTZ(PTZCapabilities value) {
        this.ptz = value;
    }

    
    public void setExtension(CapabilitiesExtension value) {
        this.extension = value;
    }

}
