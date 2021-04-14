package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"profileToken", "configurationToken"})
@XmlRootElement(name = "AddAudioEncoderConfiguration")
public class AddAudioEncoderConfiguration {

    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;
    @XmlElement(name = "ConfigurationToken", required = true)
    protected String configurationToken;
}
