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
        name = "AnalyticsEngineConfiguration",
        propOrder = {"analyticsModule", "extension"})
public class AnalyticsEngineConfiguration {

    @XmlElement(name = "AnalyticsModule")
    protected List<Config> analyticsModule;

    
    @Getter @XmlElement(name = "Extension")
    protected AnalyticsEngineConfigurationExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<Config> getAnalyticsModule() {
        if (analyticsModule == null) {
            analyticsModule = new ArrayList<Config>();
        }
        return this.analyticsModule;
    }

    
    public void setExtension(AnalyticsEngineConfigurationExtension value) {
        this.extension = value;
    }

}
