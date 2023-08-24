package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"capabilities"})
@XmlRootElement(name = "GetServiceCapabilitiesResponse2")
public class GetServiceCapabilitiesResponse2 {


    @XmlElement(name = "Capabilities", required = true)
    protected Capabilities2 capabilities;


    public void setCapabilities(Capabilities2 value) {
        this.capabilities = value;
    }
}
