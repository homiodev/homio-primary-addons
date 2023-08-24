







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"auxiliaryCommand"})
@XmlRootElement(name = "SendAuxiliaryCommand")
public class SendAuxiliaryCommand {


    @XmlElement(name = "AuxiliaryCommand", required = true)
    protected String auxiliaryCommand;


    public void setAuxiliaryCommand(String value) {
        this.auxiliaryCommand = value;
    }
}
