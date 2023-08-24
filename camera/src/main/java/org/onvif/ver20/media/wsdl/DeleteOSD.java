package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osdToken"})
@XmlRootElement(name = "DeleteOSD")
public class DeleteOSD {

    @XmlElement(name = "OSDToken", required = true)
    protected String osdToken;


    public String getOSDToken() {
        return osdToken;
    }


    public void setOSDToken(String value) {
        this.osdToken = value;
    }
}
