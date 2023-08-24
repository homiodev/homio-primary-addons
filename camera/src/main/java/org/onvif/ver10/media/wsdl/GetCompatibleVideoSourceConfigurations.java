







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken"})
@XmlRootElement(name = "GetCompatibleVideoSourceConfigurations")
public class GetCompatibleVideoSourceConfigurations {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }
}
