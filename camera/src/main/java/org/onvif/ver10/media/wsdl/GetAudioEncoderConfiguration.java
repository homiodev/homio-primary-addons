package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurationToken"})
@XmlRootElement(name = "GetAudioEncoderConfiguration")
public class GetAudioEncoderConfiguration {

    @XmlElement(name = "ConfigurationToken", required = true)
    protected String configurationToken;
}
