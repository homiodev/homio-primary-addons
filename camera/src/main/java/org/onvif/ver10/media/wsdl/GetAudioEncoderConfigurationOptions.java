package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurationToken", "profileToken"})
@XmlRootElement(name = "GetAudioEncoderConfigurationOptions")
public class GetAudioEncoderConfigurationOptions {

    @XmlElement(name = "ConfigurationToken")
    protected String configurationToken;

    @XmlElement(name = "ProfileToken")
    protected String profileToken;
}
