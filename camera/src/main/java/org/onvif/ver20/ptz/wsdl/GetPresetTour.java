package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "presetTourToken"})
@XmlRootElement(name = "GetPresetTour")
public class GetPresetTour {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    @XmlElement(name = "PresetTourToken", required = true)
    protected String presetTourToken;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }


    public void setPresetTourToken(String value) {
        this.presetTourToken = value;
    }
}
