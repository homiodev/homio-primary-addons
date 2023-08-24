package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceToken"})
@XmlRootElement(name = "GetOptions")
public class GetOptions {

    /**
     * -- GETTER --
     *  Ruft den Wert der videoSourceToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;

    /**
     * Legt den Wert der videoSourceToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }
}
