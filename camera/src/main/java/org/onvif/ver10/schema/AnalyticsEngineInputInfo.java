package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AnalyticsEngineInputInfo",
        propOrder = {"inputInfo", "extension"})
public class AnalyticsEngineInputInfo {

    
    @XmlElement(name = "InputInfo")
    protected Config inputInfo;

    
    @XmlElement(name = "Extension")
    protected AnalyticsEngineInputInfoExtension extension;

    
    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setInputInfo(Config value) {
        this.inputInfo = value;
    }

    
    public void setExtension(AnalyticsEngineInputInfoExtension value) {
        this.extension = value;
    }

}
