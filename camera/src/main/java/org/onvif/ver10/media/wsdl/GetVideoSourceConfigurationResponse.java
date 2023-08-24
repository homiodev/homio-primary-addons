







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.VideoSourceConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "GetVideoSourceConfigurationResponse")
public class GetVideoSourceConfigurationResponse {


    @XmlElement(name = "Configuration", required = true)
    protected VideoSourceConfiguration configuration;


    public void setConfiguration(VideoSourceConfiguration value) {
        this.configuration = value;
    }
}
