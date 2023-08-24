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
        name = "RecordingJobStateTracks",
        propOrder = {"track"})
public class RecordingJobStateTracks {

    @XmlElement(name = "Track")
    protected List<RecordingJobStateTrack> track;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<RecordingJobStateTrack> getTrack() {
        if (track == null) {
            track = new ArrayList<RecordingJobStateTrack>();
        }
        return this.track;
    }

}
