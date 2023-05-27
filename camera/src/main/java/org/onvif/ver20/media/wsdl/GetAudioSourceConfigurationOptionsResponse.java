package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.AudioSourceConfigurationOptions;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"options"})
@XmlRootElement(name = "GetAudioSourceConfigurationOptionsResponse")
public class GetAudioSourceConfigurationOptionsResponse {

    @XmlElement(name = "Options", required = true)
    protected AudioSourceConfigurationOptions options;

    /**
     * Ruft den Wert der options-Eigenschaft ab.
     *
     * @return possible object is {@link AudioSourceConfigurationOptions }
     */
    public AudioSourceConfigurationOptions getOptions() {
        return options;
    }

    /**
     * Legt den Wert der options-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioSourceConfigurationOptions }
     */
    public void setOptions(AudioSourceConfigurationOptions value) {
        this.options = value;
    }
}
