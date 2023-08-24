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
        name = "AudioAttributes",
        propOrder = {"bitrate", "encoding", "samplerate", "any"})
public class AudioAttributes {

    
    @Getter @XmlElement(name = "Bitrate")
    protected Integer bitrate;

    
    @Getter @XmlElement(name = "Encoding", required = true)
    protected AudioEncoding encoding;

    
    @Getter @XmlElement(name = "Samplerate")
    protected int samplerate;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setBitrate(Integer value) {
        this.bitrate = value;
    }

    
    public void setEncoding(AudioEncoding value) {
        this.encoding = value;
    }

    
    public void setSamplerate(int value) {
        this.samplerate = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
