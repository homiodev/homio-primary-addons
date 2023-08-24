







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.SupportInformation;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"supportInformation"})
@XmlRootElement(name = "GetSystemSupportInformationResponse")
public class GetSystemSupportInformationResponse {


    @XmlElement(name = "SupportInformation", required = true)
    protected SupportInformation supportInformation;


    public void setSupportInformation(SupportInformation value) {
        this.supportInformation = value;
    }
}
