package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"protocol", "profileToken"})
@XmlRootElement(name = "GetStreamUri")
public class GetStreamUri {

    @XmlElement(name = "Protocol", required = true)
    protected String protocol;

    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    /**
     * Ruft den Wert der protocol-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Legt den Wert der protocol-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProtocol(String value) {
        this.protocol = value;
    }

    /**
     * Ruft den Wert der profileToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getProfileToken() {
        return profileToken;
    }

    /**
     * Legt den Wert der profileToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfileToken(String value) {
        this.profileToken = value;
    }
}