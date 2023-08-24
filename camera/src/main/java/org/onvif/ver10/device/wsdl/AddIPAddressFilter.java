







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.IPAddressFilter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"ipAddressFilter"})
@XmlRootElement(name = "AddIPAddressFilter")
public class AddIPAddressFilter {

    @XmlElement(name = "IPAddressFilter", required = true)
    protected IPAddressFilter ipAddressFilter;


    public IPAddressFilter getIPAddressFilter() {
        return ipAddressFilter;
    }


    public void setIPAddressFilter(IPAddressFilter value) {
        this.ipAddressFilter = value;
    }
}
