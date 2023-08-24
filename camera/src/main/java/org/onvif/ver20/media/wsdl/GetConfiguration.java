package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "GetConfiguration",
        propOrder = {"configurationToken", "profileToken"})
public class GetConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der configurationToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "ConfigurationToken")
    protected String configurationToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der profileToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "ProfileToken")
    protected String profileToken;

    /**
     * Legt den Wert der configurationToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setConfigurationToken(String value) {
        this.configurationToken = value;
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
