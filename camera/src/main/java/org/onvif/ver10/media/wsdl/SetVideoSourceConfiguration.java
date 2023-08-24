







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.VideoSourceConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration", "forcePersistence"})
@XmlRootElement(name = "SetVideoSourceConfiguration")
public class SetVideoSourceConfiguration {


    @XmlElement(name = "Configuration", required = true)
    protected VideoSourceConfiguration configuration;


    @XmlElement(name = "ForcePersistence")
    protected boolean forcePersistence;


    public void setConfiguration(VideoSourceConfiguration value) {
        this.configuration = value;
    }


    public void setForcePersistence(boolean value) {
        this.forcePersistence = value;
    }
}
