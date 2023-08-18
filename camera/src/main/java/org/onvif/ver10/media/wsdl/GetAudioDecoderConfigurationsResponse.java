package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetAudioDecoderConfigurationsResponse")
public class GetAudioDecoderConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<AudioDecoderConfiguration> configurations;

    public List<AudioDecoderConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<>();
        }
        return this.configurations;
    }
}
