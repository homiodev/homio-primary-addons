







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioOutputConfigurationOptions;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetAudioOutputConfigurationOptionsResponse")
public class GetAudioOutputConfigurationOptionsResponse {


    @XmlElement(name = "Options", required = true)
    protected AudioOutputConfigurationOptions options;


    public void setOptions(AudioOutputConfigurationOptions value) {
        this.options = value;
    }
}
