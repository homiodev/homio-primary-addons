package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.ImagingSettings20;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceToken", "imagingSettings", "forcePersistence"})
@XmlRootElement(name = "SetImagingSettings")
public class SetImagingSettings {

    
    @Getter @XmlElement(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;

    
    @Getter @XmlElement(name = "ImagingSettings", required = true)
    protected ImagingSettings20 imagingSettings;

    @XmlElement(name = "ForcePersistence")
    protected Boolean forcePersistence;

    
    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }

    
    public void setImagingSettings(ImagingSettings20 value) {
        this.imagingSettings = value;
    }

    
    public Boolean isForcePersistence() {
        return forcePersistence;
    }

    
    public void setForcePersistence(Boolean value) {
        this.forcePersistence = value;
    }
}
