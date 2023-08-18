package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osdToken"})
@XmlRootElement(name = "CreateOSDResponse")
public class CreateOSDResponse {

    @XmlElement(name = "OSDToken", required = true)
    protected String osdToken;

    /**
     * Ruft den Wert der osdToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getOSDToken() {
        return osdToken;
    }

    /**
     * Legt den Wert der osdToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setOSDToken(String value) {
        this.osdToken = value;
    }
}
