







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.DiscoveryMode;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"discoveryMode"})
@XmlRootElement(name = "GetDiscoveryModeResponse")
public class GetDiscoveryModeResponse {


    @XmlElement(name = "DiscoveryMode", required = true)
    protected DiscoveryMode discoveryMode;


    public void setDiscoveryMode(DiscoveryMode value) {
        this.discoveryMode = value;
    }
}
