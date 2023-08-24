package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"capabilities"})
@XmlRootElement(name = "GetServiceCapabilitiesResponse")
public class GetServiceCapabilitiesResponse {

    /**
     * -- GETTER --
     *  Ruft den Wert der capabilities-Eigenschaft ab.
     *
     * @return possible object is {@link Capabilities }
     */
    @XmlElement(name = "Capabilities", required = true)
    protected Capabilities capabilities;

    /**
     * Legt den Wert der capabilities-Eigenschaft fest.
     *
     * @param value allowed object is {@link Capabilities }
     */
    public void setCapabilities(Capabilities value) {
        this.capabilities = value;
    }
}
