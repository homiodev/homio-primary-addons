package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"reboot"})
@XmlRootElement(name = "SetVideoSourceModeResponse")
public class SetVideoSourceModeResponse {


    @XmlElement(name = "Reboot")
    protected boolean reboot;


    public void setReboot(boolean value) {
        this.reboot = value;
    }
}
