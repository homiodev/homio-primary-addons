







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.Profile;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profiles"})
@XmlRootElement(name = "GetProfilesResponse")
public class GetProfilesResponse {

    @XmlElement(name = "Profiles")
    protected List<Profile> profiles;


    public List<Profile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<Profile>();
        }
        return this.profiles;
    }
}
