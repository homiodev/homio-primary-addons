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
        name = "AudioOutputConfigurationOptions",
        propOrder = {"outputTokensAvailable", "sendPrimacyOptions", "outputLevelRange", "any"})
public class AudioOutputConfigurationOptions {

    @XmlElement(name = "OutputTokensAvailable", required = true)
    protected List<String> outputTokensAvailable;

    @XmlElement(name = "SendPrimacyOptions")
    @XmlSchemaType(name = "anyURI")
    protected List<String> sendPrimacyOptions;

    
    @Getter @XmlElement(name = "OutputLevelRange", required = true)
    protected IntRange outputLevelRange;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<String> getOutputTokensAvailable() {
        if (outputTokensAvailable == null) {
            outputTokensAvailable = new ArrayList<String>();
        }
        return this.outputTokensAvailable;
    }

    
    public List<String> getSendPrimacyOptions() {
        if (sendPrimacyOptions == null) {
            sendPrimacyOptions = new ArrayList<String>();
        }
        return this.sendPrimacyOptions;
    }

    
    public void setOutputLevelRange(IntRange value) {
        this.outputLevelRange = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
