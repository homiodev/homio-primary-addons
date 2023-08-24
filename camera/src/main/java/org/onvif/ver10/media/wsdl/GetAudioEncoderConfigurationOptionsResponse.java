







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioEncoderConfigurationOptions;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetAudioEncoderConfigurationOptionsResponse")
public class GetAudioEncoderConfigurationOptionsResponse {


    @XmlElement(name = "Options", required = true)
    protected AudioEncoderConfigurationOptions options;


    public void setOptions(AudioEncoderConfigurationOptions value) {
        this.options = value;
    }
}
