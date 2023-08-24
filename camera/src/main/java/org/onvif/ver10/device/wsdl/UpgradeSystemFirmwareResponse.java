







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"message"})
@XmlRootElement(name = "UpgradeSystemFirmwareResponse")
public class UpgradeSystemFirmwareResponse {

    
    @XmlElement(name = "Message")
    protected String message;

    
    public void setMessage(String value) {
        this.message = value;
    }
}
