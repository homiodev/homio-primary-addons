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
        name = "AudioEncoderConfigurationOptions",
        propOrder = {"options"})
public class AudioEncoderConfigurationOptions {

    @XmlElement(name = "Options")
    protected List<AudioEncoderConfigurationOption> options;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<AudioEncoderConfigurationOption> getOptions() {
        if (options == null) {
            options = new ArrayList<AudioEncoderConfigurationOption>();
        }
        return this.options;
    }

}
