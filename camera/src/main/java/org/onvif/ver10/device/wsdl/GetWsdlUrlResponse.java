







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"wsdlUrl"})
@XmlRootElement(name = "GetWsdlUrlResponse")
public class GetWsdlUrlResponse {


    @XmlElement(name = "WsdlUrl", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String wsdlUrl;


    public void setWsdlUrl(String value) {
        this.wsdlUrl = value;
    }
}
