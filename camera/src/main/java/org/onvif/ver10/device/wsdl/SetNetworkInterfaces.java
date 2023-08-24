







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.NetworkInterfaceSetConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"interfaceToken", "networkInterface"})
@XmlRootElement(name = "SetNetworkInterfaces")
public class SetNetworkInterfaces {


    @XmlElement(name = "InterfaceToken", required = true)
    protected String interfaceToken;


    @XmlElement(name = "NetworkInterface", required = true)
    protected NetworkInterfaceSetConfiguration networkInterface;


    public void setInterfaceToken(String value) {
        this.interfaceToken = value;
    }


    public void setNetworkInterface(NetworkInterfaceSetConfiguration value) {
        this.networkInterface = value;
    }
}
