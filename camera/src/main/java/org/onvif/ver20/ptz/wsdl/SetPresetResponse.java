package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"presetToken"})
@XmlRootElement(name = "SetPresetResponse")
public class SetPresetResponse {


    @XmlElement(name = "PresetToken", required = true)
    protected String presetToken;


    public void setPresetToken(String value) {
        this.presetToken = value;
    }
}
