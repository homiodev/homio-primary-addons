package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.PresetTour;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"presetTour"})
@XmlRootElement(name = "GetPresetToursResponse")
public class GetPresetToursResponse {

    @XmlElement(name = "PresetTour")
    protected List<PresetTour> presetTour;


    public List<PresetTour> getPresetTour() {
        if (presetTour == null) {
            presetTour = new ArrayList<PresetTour>();
        }
        return this.presetTour;
    }
}
