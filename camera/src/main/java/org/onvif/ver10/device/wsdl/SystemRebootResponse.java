







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"message"})
@XmlRootElement(name = "SystemRebootResponse")
public class SystemRebootResponse {


    @XmlElement(name = "Message", required = true)
    protected String message;


    public void setMessage(String value) {
        this.message = value;
    }
}
