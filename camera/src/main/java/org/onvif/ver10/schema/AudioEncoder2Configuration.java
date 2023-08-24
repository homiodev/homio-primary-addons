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
        name = "AudioEncoder2Configuration",
        propOrder = {"encoding", "multicast", "bitrate", "sampleRate", "any"})
public class AudioEncoder2Configuration extends ConfigurationEntity {

    
    @Getter @XmlElement(name = "Encoding", required = true)
    protected String encoding;

    
    @Getter @XmlElement(name = "Multicast")
    protected MulticastConfiguration multicast;

    
    @Getter @XmlElement(name = "Bitrate")
    protected int bitrate;

    
    @Getter @XmlElement(name = "SampleRate")
    protected int sampleRate;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setEncoding(String value) {
        this.encoding = value;
    }

    
    public void setMulticast(MulticastConfiguration value) {
        this.multicast = value;
    }

    
    public void setBitrate(int value) {
        this.bitrate = value;
    }

    
    public void setSampleRate(int value) {
        this.sampleRate = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
