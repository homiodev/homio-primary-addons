







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"rebootNeeded"})
@XmlRootElement(name = "SetNetworkInterfacesResponse")
public class SetNetworkInterfacesResponse {


    @XmlElement(name = "RebootNeeded")
    protected boolean rebootNeeded;


    public void setRebootNeeded(boolean value) {
        this.rebootNeeded = value;
    }
}
