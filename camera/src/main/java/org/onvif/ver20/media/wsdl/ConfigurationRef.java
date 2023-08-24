package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ConfigurationRef",
        propOrder = {"type", "token"})
public class ConfigurationRef {

    /**
     * -- GETTER --
     *  Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Type", required = true)
    protected String type;

    /**
     * -- GETTER --
     *  Ruft den Wert der token-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Token")
    protected String token;

    /**
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Legt den Wert der token-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setToken(String value) {
        this.token = value;
    }
}
