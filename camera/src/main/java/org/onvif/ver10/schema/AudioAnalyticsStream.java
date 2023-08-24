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
        name = "AudioAnalyticsStream",
        propOrder = {"audioDescriptor", "extension"})
public class AudioAnalyticsStream {

    @XmlElement(name = "AudioDescriptor")
    protected List<AudioDescriptor> audioDescriptor;

    
    @Getter @XmlElement(name = "Extension")
    protected AudioAnalyticsStreamExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<AudioDescriptor> getAudioDescriptor() {
        if (audioDescriptor == null) {
            audioDescriptor = new ArrayList<AudioDescriptor>();
        }
        return this.audioDescriptor;
    }

    
    public void setExtension(AudioAnalyticsStreamExtension value) {
        this.extension = value;
    }

}
