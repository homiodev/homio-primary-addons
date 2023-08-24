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
        name = "AudioEncoder2ConfigurationOptions",
        propOrder = {"encoding", "bitrateList", "sampleRateList", "any"})
public class AudioEncoder2ConfigurationOptions {

    
    @Getter @XmlElement(name = "Encoding", required = true)
    protected String encoding;

    
    @Getter @XmlElement(name = "BitrateList", required = true)
    protected IntList bitrateList;

    
    @Getter @XmlElement(name = "SampleRateList", required = true)
    protected IntList sampleRateList;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setEncoding(String value) {
        this.encoding = value;
    }

    
    public void setBitrateList(IntList value) {
        this.bitrateList = value;
    }

    
    public void setSampleRateList(IntList value) {
        this.sampleRateList = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
