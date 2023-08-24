







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"any"})
@XmlRootElement(name = "SetOSDResponse")
public class SetOSDResponse {

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
