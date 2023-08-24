







package org.onvif.ver10.device.wsdl;

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
    protected DeviceServiceCapabilities capabilities;


    public void setCapabilities(DeviceServiceCapabilities value) {
        this.capabilities = value;
    }
}
