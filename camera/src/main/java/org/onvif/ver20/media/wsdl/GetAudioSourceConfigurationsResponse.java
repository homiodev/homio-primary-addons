package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.AudioSourceConfiguration;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetAudioSourceConfigurationsResponse")
public class GetAudioSourceConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<AudioSourceConfiguration> configurations;


    public List<AudioSourceConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<AudioSourceConfiguration>();
        }
        return this.configurations;
    }
}
