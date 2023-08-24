package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PTZPresetTourOptions;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetPresetTourOptionsResponse")
public class GetPresetTourOptionsResponse {


    @XmlElement(name = "Options", required = true)
    protected PTZPresetTourOptions options;


    public void setOptions(PTZPresetTourOptions value) {
        this.options = value;
    }
}
