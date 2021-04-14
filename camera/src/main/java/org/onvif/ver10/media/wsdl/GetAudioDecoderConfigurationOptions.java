package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"configurationToken", "profileToken"})
@XmlRootElement(name = "GetAudioDecoderConfigurationOptions")
public class GetAudioDecoderConfigurationOptions {

    @XmlElement(name = "ConfigurationToken")
    protected String configurationToken;
    @XmlElement(name = "ProfileToken")
    protected String profileToken;
}
