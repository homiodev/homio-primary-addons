package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.PTZConfiguration;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"ptzConfiguration"})
@XmlRootElement(name = "GetConfigurationResponse")
public class GetConfigurationResponse {

    @XmlElement(name = "PTZConfiguration", required = true)
    protected PTZConfiguration ptzConfiguration;


    public PTZConfiguration getPTZConfiguration() {
        return ptzConfiguration;
    }


    public void setPTZConfiguration(PTZConfiguration value) {
        this.ptzConfiguration = value;
    }
}
