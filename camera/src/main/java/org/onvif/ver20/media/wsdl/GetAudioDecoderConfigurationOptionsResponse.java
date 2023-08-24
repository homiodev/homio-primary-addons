package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.AudioEncoder2ConfigurationOptions;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetAudioDecoderConfigurationOptionsResponse")
public class GetAudioDecoderConfigurationOptionsResponse {

    @XmlElement(name = "Options", required = true)
    protected List<AudioEncoder2ConfigurationOptions> options;


    public List<AudioEncoder2ConfigurationOptions> getOptions() {
        if (options == null) {
            options = new ArrayList<AudioEncoder2ConfigurationOptions>();
        }
        return this.options;
    }
}
