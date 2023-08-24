







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.NetworkGateway;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"networkGateway"})
@XmlRootElement(name = "GetNetworkDefaultGatewayResponse")
public class GetNetworkDefaultGatewayResponse {


    @XmlElement(name = "NetworkGateway", required = true)
    protected NetworkGateway networkGateway;


    public void setNetworkGateway(NetworkGateway value) {
        this.networkGateway = value;
    }
}
