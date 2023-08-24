package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "GetTracksResponseItem",
        propOrder = {"trackToken", "configuration", "any"})
public class GetTracksResponseItem {


    @XmlElement(name = "TrackToken", required = true)
    protected String trackToken;


    @Getter @XmlElement(name = "Configuration", required = true)
    protected TrackConfiguration configuration;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setTrackToken(String value) {
        this.trackToken = value;
    }


    public void setConfiguration(TrackConfiguration value) {
        this.configuration = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
