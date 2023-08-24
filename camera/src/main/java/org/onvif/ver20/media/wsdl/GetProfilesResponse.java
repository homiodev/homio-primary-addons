package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profiles"})
@XmlRootElement(name = "GetProfilesResponse")
public class GetProfilesResponse {

    @XmlElement(name = "Profiles")
    protected List<MediaProfile> profiles;


    public List<MediaProfile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<MediaProfile>();
        }
        return this.profiles;
    }
}
