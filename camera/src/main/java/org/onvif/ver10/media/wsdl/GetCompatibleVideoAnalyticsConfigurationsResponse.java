







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.VideoAnalyticsConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetCompatibleVideoAnalyticsConfigurationsResponse")
public class GetCompatibleVideoAnalyticsConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<VideoAnalyticsConfiguration> configurations;


    public List<VideoAnalyticsConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<VideoAnalyticsConfiguration>();
        }
        return this.configurations;
    }
}
