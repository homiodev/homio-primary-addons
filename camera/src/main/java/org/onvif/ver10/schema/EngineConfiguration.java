package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "EngineConfiguration",
        propOrder = {"videoAnalyticsConfiguration", "analyticsEngineInputInfo", "any"})
public class EngineConfiguration {


    @Getter @XmlElement(name = "VideoAnalyticsConfiguration", required = true)
    protected VideoAnalyticsConfiguration videoAnalyticsConfiguration;


    @Getter @XmlElement(name = "AnalyticsEngineInputInfo", required = true)
    protected AnalyticsEngineInputInfo analyticsEngineInputInfo;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setVideoAnalyticsConfiguration(VideoAnalyticsConfiguration value) {
        this.videoAnalyticsConfiguration = value;
    }


    public void setAnalyticsEngineInputInfo(AnalyticsEngineInputInfo value) {
        this.analyticsEngineInputInfo = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
