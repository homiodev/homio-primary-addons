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
        name = "RecordingJobSource",
        propOrder = {"sourceToken", "autoCreateReceiver", "tracks", "extension"})
public class RecordingJobSource {


    @Getter @XmlElement(name = "SourceToken")
    protected SourceReference sourceToken;

    @XmlElement(name = "AutoCreateReceiver")
    protected Boolean autoCreateReceiver;

    @XmlElement(name = "Tracks")
    protected List<RecordingJobTrack> tracks;


    @Getter @XmlElement(name = "Extension")
    protected RecordingJobSourceExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setSourceToken(SourceReference value) {
        this.sourceToken = value;
    }


    public Boolean isAutoCreateReceiver() {
        return autoCreateReceiver;
    }


    public void setAutoCreateReceiver(Boolean value) {
        this.autoCreateReceiver = value;
    }


    public List<RecordingJobTrack> getTracks() {
        if (tracks == null) {
            tracks = new ArrayList<RecordingJobTrack>();
        }
        return this.tracks;
    }


    public void setExtension(RecordingJobSourceExtension value) {
        this.extension = value;
    }

}
