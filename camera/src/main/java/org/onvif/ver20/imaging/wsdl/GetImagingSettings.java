package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceToken"})
@XmlRootElement(name = "GetImagingSettings")
public class GetImagingSettings {

    
    @XmlElement(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;

    
    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }
}
