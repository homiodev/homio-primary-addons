package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.VideoEncoder2Configuration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetVideoEncoderConfigurationsResponse")
public class GetVideoEncoderConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<VideoEncoder2Configuration> configurations;


    public List<VideoEncoder2Configuration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<VideoEncoder2Configuration>();
        }
        return this.configurations;
    }
}
