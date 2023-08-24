







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.AudioEncoderConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetAudioEncoderConfigurationsResponse")
public class GetAudioEncoderConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<AudioEncoderConfiguration> configurations;


    public List<AudioEncoderConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<AudioEncoderConfiguration>();
        }
        return this.configurations;
    }
}
