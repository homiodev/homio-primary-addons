







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.VideoAnalyticsConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "GetVideoAnalyticsConfigurationResponse")
public class GetVideoAnalyticsConfigurationResponse {


    @XmlElement(name = "Configuration", required = true)
    protected VideoAnalyticsConfiguration configuration;


    public void setConfiguration(VideoAnalyticsConfiguration value) {
        this.configuration = value;
    }
}
