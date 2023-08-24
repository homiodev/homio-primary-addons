package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoAnalyticsStream",
        propOrder = {"frameOrExtension"})
public class VideoAnalyticsStream {

    @XmlElements({
            @XmlElement(name = "Frame", type = Frame.class),
            @XmlElement(name = "Extension", type = VideoAnalyticsStreamExtension.class)
    })
    protected List<java.lang.Object> frameOrExtension;


    public List<java.lang.Object> getFrameOrExtension() {
        if (frameOrExtension == null) {
            frameOrExtension = new ArrayList<>();
        }
        return this.frameOrExtension;
    }
}
