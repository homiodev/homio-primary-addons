package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "presetToken"})
@XmlRootElement(name = "RemovePreset")
public class RemovePreset {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    @XmlElement(name = "PresetToken", required = true)
    protected String presetToken;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }


    public void setPresetToken(String value) {
        this.presetToken = value;
    }
}
