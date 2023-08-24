







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.OSDConfiguration;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osd", "any"})
@XmlRootElement(name = "GetOSDResponse")
public class GetOSDResponse {

    @XmlElement(name = "OSD", required = true)
    protected OSDConfiguration osd;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public OSDConfiguration getOSD() {
        return osd;
    }


    public void setOSD(OSDConfiguration value) {
        this.osd = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
