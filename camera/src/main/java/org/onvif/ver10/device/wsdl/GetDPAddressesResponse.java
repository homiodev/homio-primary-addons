







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.NetworkHost;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"dpAddress"})
@XmlRootElement(name = "GetDPAddressesResponse")
public class GetDPAddressesResponse {

    @XmlElement(name = "DPAddress")
    protected List<NetworkHost> dpAddress;


    public List<NetworkHost> getDPAddress() {
        if (dpAddress == null) {
            dpAddress = new ArrayList<NetworkHost>();
        }
        return this.dpAddress;
    }
}
