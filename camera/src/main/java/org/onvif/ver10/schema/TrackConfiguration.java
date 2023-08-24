package org.onvif.ver10.schema;

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
        name = "TrackConfiguration",
        propOrder = {"trackType", "description", "any"})
public class TrackConfiguration {


    @Getter @XmlElement(name = "TrackType", required = true)
    protected TrackType trackType;


    @Getter @XmlElement(name = "Description", required = true)
    protected String description;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setTrackType(TrackType value) {
        this.trackType = value;
    }


    public void setDescription(String value) {
        this.description = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
