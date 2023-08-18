package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "configurationToken"})
@XmlRootElement(name = "AddVideoSourceConfiguration")
public class AddVideoSourceConfiguration {

    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    @XmlElement(name = "ConfigurationToken", required = true)
    protected String configurationToken;
}
