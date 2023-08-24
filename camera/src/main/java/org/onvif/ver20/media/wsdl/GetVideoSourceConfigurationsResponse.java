package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.VideoSourceConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetVideoSourceConfigurationsResponse")
public class GetVideoSourceConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<VideoSourceConfiguration> configurations;


    public List<VideoSourceConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<VideoSourceConfiguration>();
        }
        return this.configurations;
    }
}
