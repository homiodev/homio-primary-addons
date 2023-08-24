







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.OSDConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osDs"})
@XmlRootElement(name = "GetOSDsResponse")
public class GetOSDsResponse {

    @XmlElement(name = "OSDs")
    protected List<OSDConfiguration> osDs;


    public List<OSDConfiguration> getOSDs() {
        if (osDs == null) {
            osDs = new ArrayList<OSDConfiguration>();
        }
        return this.osDs;
    }
}
