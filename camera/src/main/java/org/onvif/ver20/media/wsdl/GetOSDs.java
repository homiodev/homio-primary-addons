package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osdToken", "configurationToken"})
@XmlRootElement(name = "GetOSDs")
public class GetOSDs {

    @XmlElement(name = "OSDToken")
    protected String osdToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der configurationToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "ConfigurationToken")
    protected String configurationToken;

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

    /**
     * Legt den Wert der configurationToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setConfigurationToken(String value) {
        this.configurationToken = value;
    }
}
