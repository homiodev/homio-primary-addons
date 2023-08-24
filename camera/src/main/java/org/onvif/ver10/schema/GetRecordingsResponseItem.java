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
        name = "GetRecordingsResponseItem",
        propOrder = {"recordingToken", "configuration", "tracks", "any"})
public class GetRecordingsResponseItem {


    @Getter @XmlElement(name = "RecordingToken", required = true)
    protected String recordingToken;


    @Getter @XmlElement(name = "Configuration", required = true)
    protected RecordingConfiguration configuration;


    @Getter @XmlElement(name = "Tracks", required = true)
    protected GetTracksResponseList tracks;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setRecordingToken(String value) {
        this.recordingToken = value;
    }


    public void setConfiguration(RecordingConfiguration value) {
        this.configuration = value;
    }


    public void setTracks(GetTracksResponseList value) {
        this.tracks = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
