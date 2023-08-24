







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"auxiliaryCommandResponse"})
@XmlRootElement(name = "SendAuxiliaryCommandResponse")
public class SendAuxiliaryCommandResponse {


    @XmlElement(name = "AuxiliaryCommandResponse")
    protected String auxiliaryCommandResponse;


    public void setAuxiliaryCommandResponse(String value) {
        this.auxiliaryCommandResponse = value;
    }
}
