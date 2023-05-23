package org.onvif.ver20.media.wsdl;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"profiles"})
@XmlRootElement(name = "GetProfilesResponse")
public class GetProfilesResponse {

    @XmlElement(name = "Profiles")
    protected List<MediaProfile> profiles;

    /**
     * Gets the value of the profiles property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the profiles
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     *    getProfiles().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link MediaProfile }
     */
    public List<MediaProfile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<MediaProfile>();
        }
        return this.profiles;
    }
}
