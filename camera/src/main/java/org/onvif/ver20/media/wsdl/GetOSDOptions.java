package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"configurationToken"})
@XmlRootElement(name = "GetOSDOptions")
public class GetOSDOptions {

    @XmlElement(name = "ConfigurationToken", required = true)
    protected String configurationToken;

    /**
     * Ruft den Wert der configurationToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getConfigurationToken() {
        return configurationToken;
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