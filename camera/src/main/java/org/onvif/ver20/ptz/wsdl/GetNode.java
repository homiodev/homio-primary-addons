package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"nodeToken"})
@XmlRootElement(name = "GetNode")
public class GetNode {


    @XmlElement(name = "NodeToken", required = true)
    protected String nodeToken;


    public void setNodeToken(String value) {
        this.nodeToken = value;
    }
}
