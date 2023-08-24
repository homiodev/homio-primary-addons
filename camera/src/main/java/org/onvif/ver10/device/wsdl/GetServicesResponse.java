







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"service"})
@XmlRootElement(name = "GetServicesResponse")
public class GetServicesResponse {

    @XmlElement(name = "Service", required = true)
    protected List<Service> service;


    public List<Service> getService() {
        if (service == null) {
            service = new ArrayList<Service>();
        }
        return this.service;
    }
}
