package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceToken", "videoSourceModeToken"})
@XmlRootElement(name = "SetVideoSourceMode")
public class SetVideoSourceMode {


    @XmlElement(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;


    @XmlElement(name = "VideoSourceModeToken", required = true)
    protected String videoSourceModeToken;


    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }


    public void setVideoSourceModeToken(String value) {
        this.videoSourceModeToken = value;
    }
}
