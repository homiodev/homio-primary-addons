package org.onvif.ver20.imaging.wsdl;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "capabilities"
})
@XmlRootElement(name = "GetServiceCapabilitiesResponse")
public class GetServiceCapabilitiesResponse {

    @XmlElement(name = "Capabilities", required = true)
    protected Capabilities capabilities;

    /**
     * Ruft den Wert der capabilities-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Capabilities }
     *
     */
    public Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Legt den Wert der capabilities-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Capabilities }
     *
     */
    public void setCapabilities(Capabilities value) {
        this.capabilities = value;
    }

}
