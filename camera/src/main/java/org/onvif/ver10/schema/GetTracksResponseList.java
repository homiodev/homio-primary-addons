package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "GetTracksResponseList",
        propOrder = {"track"})
public class GetTracksResponseList {

    @XmlElement(name = "Track")
    protected List<GetTracksResponseItem> track;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<GetTracksResponseItem> getTrack() {
        if (track == null) {
            track = new ArrayList<GetTracksResponseItem>();
        }
        return this.track;
    }

}
