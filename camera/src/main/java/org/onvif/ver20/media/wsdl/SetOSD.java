package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.OSDConfiguration;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osd"})
@XmlRootElement(name = "SetOSD")
public class SetOSD {

    @XmlElement(name = "OSD", required = true)
    protected OSDConfiguration osd;


    public OSDConfiguration getOSD() {
        return osd;
    }


    public void setOSD(OSDConfiguration value) {
        this.osd = value;
    }
}
