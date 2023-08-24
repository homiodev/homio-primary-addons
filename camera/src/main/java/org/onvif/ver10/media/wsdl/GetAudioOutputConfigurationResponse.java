







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioOutputConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "GetAudioOutputConfigurationResponse")
public class GetAudioOutputConfigurationResponse {


    @XmlElement(name = "Configuration", required = true)
    protected AudioOutputConfiguration configuration;


    public void setConfiguration(AudioOutputConfiguration value) {
        this.configuration = value;
    }
}
