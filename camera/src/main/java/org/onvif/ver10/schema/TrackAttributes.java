package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "TrackAttributes",
        propOrder = {
                "trackInformation",
                "videoAttributes",
                "audioAttributes",
                "metadataAttributes",
                "extension"
        })
public class TrackAttributes {


    @XmlElement(name = "TrackInformation", required = true)
    protected TrackInformation trackInformation;


    @XmlElement(name = "VideoAttributes")
    protected VideoAttributes videoAttributes;


    @XmlElement(name = "AudioAttributes")
    protected AudioAttributes audioAttributes;


    @XmlElement(name = "MetadataAttributes")
    protected MetadataAttributes metadataAttributes;


    @XmlElement(name = "Extension")
    protected TrackAttributesExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setTrackInformation(TrackInformation value) {
        this.trackInformation = value;
    }


    public void setVideoAttributes(VideoAttributes value) {
        this.videoAttributes = value;
    }


    public void setAudioAttributes(AudioAttributes value) {
        this.audioAttributes = value;
    }


    public void setMetadataAttributes(MetadataAttributes value) {
        this.metadataAttributes = value;
    }


    public void setExtension(TrackAttributesExtension value) {
        this.extension = value;
    }

}
