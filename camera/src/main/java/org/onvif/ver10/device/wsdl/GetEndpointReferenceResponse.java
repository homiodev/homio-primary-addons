







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"guid", "any"})
@XmlRootElement(name = "GetEndpointReferenceResponse")
public class GetEndpointReferenceResponse {

    @XmlElement(name = "GUID", required = true)
    protected String guid;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public String getGUID() {
        return guid;
    }


    public void setGUID(String value) {
        this.guid = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
