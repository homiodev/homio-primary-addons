package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioSourceConfigurationOptions;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetAudioSourceConfigurationOptionsResponse")
public class GetAudioSourceConfigurationOptionsResponse {


    @XmlElement(name = "Options", required = true)
    protected AudioSourceConfigurationOptions options;


    public void setOptions(AudioSourceConfigurationOptions value) {
        this.options = value;
    }
}
