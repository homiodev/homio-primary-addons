package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"configurations"})
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
