







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.VideoEncoderConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetVideoEncoderConfigurationsResponse")
public class GetVideoEncoderConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<VideoEncoderConfiguration> configurations;

    
    public List<VideoEncoderConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<VideoEncoderConfiguration>();
        }
        return this.configurations;
    }
}
