package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeviceEntity")
@XmlSeeAlso({
        RelayOutput.class,
        NetworkInterface.class,
        VideoOutput.class,
        AudioSource.class,
        OSDConfiguration.class,
        AudioOutput.class,
        VideoSource.class,
        DigitalInput.class,
        PTZNode.class
})
public class DeviceEntity {

    /**
     * -- GETTER --
     *  Ruft den Wert der token-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "token", required = true)
    protected String token;

    /**
     * Legt den Wert der token-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setToken(String value) {
        this.token = value;
    }
}
