package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken"})
@XmlRootElement(name = "GetPresetTours")
public class GetPresetTours {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }
}
