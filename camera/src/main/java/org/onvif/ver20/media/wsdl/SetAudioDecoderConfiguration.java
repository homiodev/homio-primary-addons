package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "SetAudioDecoderConfiguration")
public class SetAudioDecoderConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der configuration-Eigenschaft ab.
     *
     * @return possible object is {@link AudioDecoderConfiguration }
     */
    @XmlElement(name = "Configuration", required = true)
    protected AudioDecoderConfiguration configuration;

    /**
     * Legt den Wert der configuration-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioDecoderConfiguration }
     */
    public void setConfiguration(AudioDecoderConfiguration value) {
        this.configuration = value;
    }
}
