







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioSourceConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "GetAudioSourceConfigurationResponse")
public class GetAudioSourceConfigurationResponse {

    
    @XmlElement(name = "Configuration", required = true)
    protected AudioSourceConfiguration configuration;

    
    public void setConfiguration(AudioSourceConfiguration value) {
        this.configuration = value;
    }
}
