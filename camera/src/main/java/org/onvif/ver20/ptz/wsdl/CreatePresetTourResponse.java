package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"presetTourToken"})
@XmlRootElement(name = "CreatePresetTourResponse")
public class CreatePresetTourResponse {


    @XmlElement(name = "PresetTourToken", required = true)
    protected String presetTourToken;


    public void setPresetTourToken(String value) {
        this.presetTourToken = value;
    }
}
