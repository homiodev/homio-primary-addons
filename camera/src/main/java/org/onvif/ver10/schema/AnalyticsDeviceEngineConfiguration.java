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
        name = "AnalyticsDeviceEngineConfiguration",
        propOrder = {"engineConfiguration", "extension"})
public class AnalyticsDeviceEngineConfiguration {

    @XmlElement(name = "EngineConfiguration", required = true)
    protected List<EngineConfiguration> engineConfiguration;

    
    @Getter @XmlElement(name = "Extension")
    protected AnalyticsDeviceEngineConfigurationExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<EngineConfiguration> getEngineConfiguration() {
        if (engineConfiguration == null) {
            engineConfiguration = new ArrayList<EngineConfiguration>();
        }
        return this.engineConfiguration;
    }

    
    public void setExtension(AnalyticsDeviceEngineConfigurationExtension value) {
        this.extension = value;
    }

}
