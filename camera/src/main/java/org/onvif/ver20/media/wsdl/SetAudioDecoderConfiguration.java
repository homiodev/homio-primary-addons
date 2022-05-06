package org.onvif.ver20.media.wsdl;

import org.onvif.ver10.schema.AudioDecoderConfiguration;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "configuration"
})
@XmlRootElement(name = "SetAudioDecoderConfiguration")
public class SetAudioDecoderConfiguration {

    @XmlElement(name = "Configuration", required = true)
    protected AudioDecoderConfiguration configuration;

    /**
     * Ruft den Wert der configuration-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link AudioDecoderConfiguration }
     *
     */
    public AudioDecoderConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Legt den Wert der configuration-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link AudioDecoderConfiguration }
     *
     */
    public void setConfiguration(AudioDecoderConfiguration value) {
        this.configuration = value;
    }

}
