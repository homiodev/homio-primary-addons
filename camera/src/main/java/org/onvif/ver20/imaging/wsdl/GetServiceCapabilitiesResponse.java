package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"capabilities"})
@XmlRootElement(name = "GetServiceCapabilitiesResponse")
public class GetServiceCapabilitiesResponse {


    @XmlElement(name = "Capabilities", required = true)
    protected Capabilities capabilities;


    public void setCapabilities(Capabilities value) {
        this.capabilities = value;
    }
}
