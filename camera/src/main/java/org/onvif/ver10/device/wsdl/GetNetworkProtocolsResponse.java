







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.NetworkProtocol;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"networkProtocols"})
@XmlRootElement(name = "GetNetworkProtocolsResponse")
public class GetNetworkProtocolsResponse {

    @XmlElement(name = "NetworkProtocols")
    protected List<NetworkProtocol> networkProtocols;


    public List<NetworkProtocol> getNetworkProtocols() {
        if (networkProtocols == null) {
            networkProtocols = new ArrayList<NetworkProtocol>();
        }
        return this.networkProtocols;
    }
}
