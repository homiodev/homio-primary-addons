package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.AudioOutputConfigurationOptions;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"options"})
@XmlRootElement(name = "GetAudioOutputConfigurationOptionsResponse")
public class GetAudioOutputConfigurationOptionsResponse {

    @XmlElement(name = "Options", required = true)
    protected AudioOutputConfigurationOptions options;

    /**
     * Ruft den Wert der options-Eigenschaft ab.
     *
     * @return possible object is {@link AudioOutputConfigurationOptions }
     */
    public AudioOutputConfigurationOptions getOptions() {
        return options;
    }

    /**
     * Legt den Wert der options-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioOutputConfigurationOptions }
     */
    public void setOptions(AudioOutputConfigurationOptions value) {
        this.options = value;
    }
}
