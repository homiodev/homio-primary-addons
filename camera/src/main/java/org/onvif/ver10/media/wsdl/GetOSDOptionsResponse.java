







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.OSDConfigurationOptions;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osdOptions", "any"})
@XmlRootElement(name = "GetOSDOptionsResponse")
public class GetOSDOptionsResponse {

    @XmlElement(name = "OSDOptions", required = true)
    protected OSDConfigurationOptions osdOptions;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public OSDConfigurationOptions getOSDOptions() {
        return osdOptions;
    }


    public void setOSDOptions(OSDConfigurationOptions value) {
        this.osdOptions = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
