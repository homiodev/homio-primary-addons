package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken"})
@XmlRootElement(name = "SetSynchronizationPoint")
public class SetSynchronizationPoint {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }
}
