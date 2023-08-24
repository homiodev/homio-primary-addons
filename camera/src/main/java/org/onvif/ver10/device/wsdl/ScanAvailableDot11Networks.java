







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"interfaceToken"})
@XmlRootElement(name = "ScanAvailableDot11Networks")
public class ScanAvailableDot11Networks {


    @XmlElement(name = "InterfaceToken", required = true)
    protected String interfaceToken;


    public void setInterfaceToken(String value) {
        this.interfaceToken = value;
    }
}
