package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.PTZConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"ptzConfiguration"})
@XmlRootElement(name = "GetConfigurationsResponse")
public class GetConfigurationsResponse {

    @XmlElement(name = "PTZConfiguration")
    protected List<PTZConfiguration> ptzConfiguration;


    public List<PTZConfiguration> getPTZConfiguration() {
        if (ptzConfiguration == null) {
            ptzConfiguration = new ArrayList<PTZConfiguration>();
        }
        return this.ptzConfiguration;
    }
}
