package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "SetAudioDecoderConfiguration")
public class SetAudioDecoderConfiguration {


    @XmlElement(name = "Configuration", required = true)
    protected AudioDecoderConfiguration configuration;


    public void setConfiguration(AudioDecoderConfiguration value) {
        this.configuration = value;
    }
}
