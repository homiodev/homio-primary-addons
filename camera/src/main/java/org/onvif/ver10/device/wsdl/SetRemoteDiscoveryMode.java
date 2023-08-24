







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.DiscoveryMode;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"remoteDiscoveryMode"})
@XmlRootElement(name = "SetRemoteDiscoveryMode")
public class SetRemoteDiscoveryMode {


    @XmlElement(name = "RemoteDiscoveryMode", required = true)
    protected DiscoveryMode remoteDiscoveryMode;


    public void setRemoteDiscoveryMode(DiscoveryMode value) {
        this.remoteDiscoveryMode = value;
    }
}
