package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PTZSpeed;
import org.onvif.ver10.schema.PTZVector;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "translation", "speed"})
@XmlRootElement(name = "RelativeMove")
public class RelativeMove {

    /**
     * -- GETTER --
     *  Ruft den Wert der profileToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der translation-Eigenschaft ab.
     *
     * @return possible object is {@link PTZVector }
     */
    @XmlElement(name = "Translation", required = true)
    protected PTZVector translation;

    /**
     * -- GETTER --
     *  Ruft den Wert der speed-Eigenschaft ab.
     *
     * @return possible object is {@link PTZSpeed }
     */
    @XmlElement(name = "Speed")
    protected PTZSpeed speed;

    /**
     * Legt den Wert der profileToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    /**
     * Legt den Wert der translation-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZVector }
     */
    public void setTranslation(PTZVector value) {
        this.translation = value;
    }

    /**
     * Legt den Wert der speed-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZSpeed }
     */
    public void setSpeed(PTZSpeed value) {
        this.speed = value;
    }
}
