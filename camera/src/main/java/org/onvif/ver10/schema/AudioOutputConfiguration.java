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
        name = "AudioOutputConfiguration",
        propOrder = {"outputToken", "sendPrimacy", "outputLevel", "any"})
public class AudioOutputConfiguration extends ConfigurationEntity {

    
    @Getter @XmlElement(name = "OutputToken", required = true)
    protected String outputToken;

    
    @Getter @XmlElement(name = "SendPrimacy")
    @XmlSchemaType(name = "anyURI")
    protected String sendPrimacy;

    
    @Getter @XmlElement(name = "OutputLevel")
    protected int outputLevel;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setOutputToken(String value) {
        this.outputToken = value;
    }

    
    public void setSendPrimacy(String value) {
        this.sendPrimacy = value;
    }

    
    public void setOutputLevel(int value) {
        this.outputLevel = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
