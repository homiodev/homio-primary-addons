







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.VideoSource;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSources"})
@XmlRootElement(name = "GetVideoSourcesResponse")
public class GetVideoSourcesResponse {

    @XmlElement(name = "VideoSources")
    protected List<VideoSource> videoSources;


    public List<VideoSource> getVideoSources() {
        if (videoSources == null) {
            videoSources = new ArrayList<VideoSource>();
        }
        return this.videoSources;
    }
}
