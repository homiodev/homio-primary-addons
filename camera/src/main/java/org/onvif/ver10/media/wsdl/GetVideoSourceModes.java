







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceToken"})
@XmlRootElement(name = "GetVideoSourceModes")
public class GetVideoSourceModes {


    @XmlElement(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;


    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }
}
