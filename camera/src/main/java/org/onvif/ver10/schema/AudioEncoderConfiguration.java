package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AudioEncoderConfiguration",
        propOrder = {"encoding", "bitrate", "sampleRate", "multicast", "sessionTimeout", "any"})
public class AudioEncoderConfiguration extends ConfigurationEntity {

    
    @Getter @XmlElement(name = "Encoding", required = true)
    protected AudioEncoding encoding;

    
    @Getter @XmlElement(name = "Bitrate")
    protected int bitrate;

    
    @Getter @XmlElement(name = "SampleRate")
    protected int sampleRate;

    
    @Getter @XmlElement(name = "Multicast", required = true)
    protected MulticastConfiguration multicast;

    
    @Getter @XmlElement(name = "SessionTimeout", required = true)
    protected Duration sessionTimeout;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setEncoding(AudioEncoding value) {
        this.encoding = value;
    }

    
    public void setBitrate(int value) {
        this.bitrate = value;
    }

    
    public void setSampleRate(int value) {
        this.sampleRate = value;
    }

    
    public void setMulticast(MulticastConfiguration value) {
        this.multicast = value;
    }

    
    public void setSessionTimeout(Duration value) {
        this.sessionTimeout = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
