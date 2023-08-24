package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PresetTour;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"presetTour"})
@XmlRootElement(name = "GetPresetTourResponse")
public class GetPresetTourResponse {


    @XmlElement(name = "PresetTour", required = true)
    protected PresetTour presetTour;


    public void setPresetTour(PresetTour value) {
        this.presetTour = value;
    }
}
