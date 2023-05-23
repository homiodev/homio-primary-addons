package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"osdToken"})
@XmlRootElement(name = "DeleteOSD")
public class DeleteOSD {

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
