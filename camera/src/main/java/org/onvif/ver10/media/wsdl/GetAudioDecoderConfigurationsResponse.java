package org.onvif.ver10.media.wsdl;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

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
