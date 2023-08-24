package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioSourceConfigurationOptions;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetAudioSourceConfigurationOptionsResponse")
public class GetAudioSourceConfigurationOptionsResponse {

    /**
     * -- GETTER --
     *  Ruft den Wert der options-Eigenschaft ab.
     *
     * @return possible object is {@link AudioSourceConfigurationOptions }
     */
    @XmlElement(name = "Options", required = true)
    protected AudioSourceConfigurationOptions options;

    /**
     * Legt den Wert der options-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioSourceConfigurationOptions }
     */
    public void setOptions(AudioSourceConfigurationOptions value) {
        this.options = value;
    }
}
