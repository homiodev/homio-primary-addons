package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.VideoEncoder2ConfigurationOptions;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetVideoEncoderConfigurationOptionsResponse")
public class GetVideoEncoderConfigurationOptionsResponse {

    @XmlElement(name = "Options", required = true)
    protected List<VideoEncoder2ConfigurationOptions> options;


    public List<VideoEncoder2ConfigurationOptions> getOptions() {
        if (options == null) {
            options = new ArrayList<VideoEncoder2ConfigurationOptions>();
        }
        return this.options;
    }
}
