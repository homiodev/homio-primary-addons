package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"protocol", "profileToken"})
@XmlRootElement(name = "GetStreamUri")
public class GetStreamUri {

    /**
     * -- GETTER --
     *  Ruft den Wert der protocol-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Protocol", required = true)
    protected String protocol;

    /**
     * -- GETTER --
     *  Ruft den Wert der profileToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    /**
     * Legt den Wert der protocol-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProtocol(String value) {
        this.protocol = value;
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
