package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceModes"})
@XmlRootElement(name = "GetVideoSourceModesResponse")
public class GetVideoSourceModesResponse {

    @XmlElement(name = "VideoSourceModes", required = true)
    protected List<VideoSourceMode> videoSourceModes;


    public List<VideoSourceMode> getVideoSourceModes() {
        if (videoSourceModes == null) {
            videoSourceModes = new ArrayList<VideoSourceMode>();
        }
        return this.videoSourceModes;
    }
}
