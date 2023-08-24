package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.PTZPreset;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"preset"})
@XmlRootElement(name = "GetPresetsResponse")
public class GetPresetsResponse {

    @XmlElement(name = "Preset")
    protected List<PTZPreset> preset;


    public List<PTZPreset> getPreset() {
        if (preset == null) {
            preset = new ArrayList<PTZPreset>();
        }
        return this.preset;
    }
}
