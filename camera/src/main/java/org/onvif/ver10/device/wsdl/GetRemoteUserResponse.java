







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.RemoteUser;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"remoteUser"})
@XmlRootElement(name = "GetRemoteUserResponse")
public class GetRemoteUserResponse {


    @XmlElement(name = "RemoteUser")
    protected RemoteUser remoteUser;


    public void setRemoteUser(RemoteUser value) {
        this.remoteUser = value;
    }
}
