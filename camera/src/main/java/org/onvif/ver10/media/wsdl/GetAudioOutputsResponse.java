







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.AudioOutput;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"audioOutputs"})
@XmlRootElement(name = "GetAudioOutputsResponse")
public class GetAudioOutputsResponse {

    @XmlElement(name = "AudioOutputs")
    protected List<AudioOutput> audioOutputs;


    public List<AudioOutput> getAudioOutputs() {
        if (audioOutputs == null) {
            audioOutputs = new ArrayList<AudioOutput>();
        }
        return this.audioOutputs;
    }
}
