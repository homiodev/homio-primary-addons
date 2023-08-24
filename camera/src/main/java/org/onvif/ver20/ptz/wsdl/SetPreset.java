package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "presetName", "presetToken"})
@XmlRootElement(name = "SetPreset")
public class SetPreset {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    @XmlElement(name = "PresetName")
    protected String presetName;


    @XmlElement(name = "PresetToken")
    protected String presetToken;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }


    public void setPresetName(String value) {
        this.presetName = value;
    }


    public void setPresetToken(String value) {
        this.presetToken = value;
    }
}
