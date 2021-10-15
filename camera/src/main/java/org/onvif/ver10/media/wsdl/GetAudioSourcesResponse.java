package org.onvif.ver10.media.wsdl;

import org.onvif.ver10.schema.AudioSource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"audioSources"})
@XmlRootElement(name = "GetAudioSourcesResponse")
public class GetAudioSourcesResponse {

    @XmlElement(name = "AudioSources")
    protected List<AudioSource> audioSources;

    public List<AudioSource> getAudioSources() {
        if (audioSources == null) {
            audioSources = new ArrayList<>();
        }
        return this.audioSources;
    }

}
