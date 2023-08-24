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
        name = "AudioSourceConfigurationOptions",
        propOrder = {"inputTokensAvailable", "extension"})
public class AudioSourceConfigurationOptions {

    @XmlElement(name = "InputTokensAvailable", required = true)
    protected List<String> inputTokensAvailable;

    
    @Getter @XmlElement(name = "Extension")
    protected AudioSourceOptionsExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<String> getInputTokensAvailable() {
        if (inputTokensAvailable == null) {
            inputTokensAvailable = new ArrayList<String>();
        }
        return this.inputTokensAvailable;
    }

    
    public void setExtension(AudioSourceOptionsExtension value) {
        this.extension = value;
    }

}
