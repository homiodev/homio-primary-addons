







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osdToken", "any"})
@XmlRootElement(name = "GetOSD")
public class GetOSD {

    @XmlElement(name = "OSDToken", required = true)
    protected String osdToken;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public String getOSDToken() {
        return osdToken;
    }


    public void setOSDToken(String value) {
        this.osdToken = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
