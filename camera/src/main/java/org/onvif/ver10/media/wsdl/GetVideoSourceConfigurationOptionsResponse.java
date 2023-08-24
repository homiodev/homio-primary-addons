







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.VideoSourceConfigurationOptions;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetVideoSourceConfigurationOptionsResponse")
public class GetVideoSourceConfigurationOptionsResponse {


    @XmlElement(name = "Options", required = true)
    protected VideoSourceConfigurationOptions options;


    public void setOptions(VideoSourceConfigurationOptions value) {
        this.options = value;
    }
}
