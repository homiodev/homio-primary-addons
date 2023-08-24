







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ProfileCapabilities",
        propOrder = {"any"})
public class ProfileCapabilities {

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    @Getter @XmlAttribute(name = "MaximumNumberOfProfiles")
    protected Integer maximumNumberOfProfiles;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }


    public void setMaximumNumberOfProfiles(Integer value) {
        this.maximumNumberOfProfiles = value;
    }

}
