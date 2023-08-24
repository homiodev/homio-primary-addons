package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"auxiliaryResponse"})
@XmlRootElement(name = "SendAuxiliaryCommandResponse")
public class SendAuxiliaryCommandResponse {

    
    @XmlElement(name = "AuxiliaryResponse", required = true)
    protected String auxiliaryResponse;

    
    public void setAuxiliaryResponse(String value) {
        this.auxiliaryResponse = value;
    }
}
