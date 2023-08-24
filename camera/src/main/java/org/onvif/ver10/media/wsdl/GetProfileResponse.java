







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.Profile;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profile"})
@XmlRootElement(name = "GetProfileResponse")
public class GetProfileResponse {


    @XmlElement(name = "Profile", required = true)
    protected Profile profile;


    public void setProfile(Profile value) {
        this.profile = value;
    }
}
