







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.NetworkInterface;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"networkInterfaces"})
@XmlRootElement(name = "GetNetworkInterfacesResponse")
public class GetNetworkInterfacesResponse {

    @XmlElement(name = "NetworkInterfaces", required = true)
    protected List<NetworkInterface> networkInterfaces;


    public List<NetworkInterface> getNetworkInterfaces() {
        if (networkInterfaces == null) {
            networkInterfaces = new ArrayList<NetworkInterface>();
        }
        return this.networkInterfaces;
    }
}
