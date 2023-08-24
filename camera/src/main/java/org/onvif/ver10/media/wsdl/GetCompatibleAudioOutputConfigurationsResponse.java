







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.AudioOutputConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetCompatibleAudioOutputConfigurationsResponse")
public class GetCompatibleAudioOutputConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<AudioOutputConfiguration> configurations;


    public List<AudioOutputConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<AudioOutputConfiguration>();
        }
        return this.configurations;
    }
}
