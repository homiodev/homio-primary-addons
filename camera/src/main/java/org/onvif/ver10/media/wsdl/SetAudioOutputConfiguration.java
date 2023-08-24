







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.AudioOutputConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration", "forcePersistence"})
@XmlRootElement(name = "SetAudioOutputConfiguration")
public class SetAudioOutputConfiguration {


    @XmlElement(name = "Configuration", required = true)
    protected AudioOutputConfiguration configuration;


    @XmlElement(name = "ForcePersistence")
    protected boolean forcePersistence;


    public void setConfiguration(AudioOutputConfiguration value) {
        this.configuration = value;
    }


    public void setForcePersistence(boolean value) {
        this.forcePersistence = value;
    }
}
