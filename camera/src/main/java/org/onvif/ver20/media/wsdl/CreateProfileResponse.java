package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"token"})
@XmlRootElement(name = "CreateProfileResponse")
public class CreateProfileResponse {


    @XmlElement(name = "Token", required = true)
    protected String token;


    public void setToken(String value) {
        this.token = value;
    }
}
