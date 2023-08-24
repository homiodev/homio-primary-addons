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
        name = "AudioClassDescriptor",
        propOrder = {"classCandidate", "extension"})
public class AudioClassDescriptor {

    @XmlElement(name = "ClassCandidate")
    protected List<AudioClassCandidate> classCandidate;

    
    @Getter @XmlElement(name = "Extension")
    protected AudioClassDescriptorExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<AudioClassCandidate> getClassCandidate() {
        if (classCandidate == null) {
            classCandidate = new ArrayList<AudioClassCandidate>();
        }
        return this.classCandidate;
    }

    
    public void setExtension(AudioClassDescriptorExtension value) {
        this.extension = value;
    }

}
