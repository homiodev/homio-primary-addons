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
        name = "SupportedAnalyticsModules",
        propOrder = {"analyticsModuleContentSchemaLocation", "analyticsModuleDescription", "extension"})
public class SupportedAnalyticsModules {

    @XmlElement(name = "AnalyticsModuleContentSchemaLocation")
    @XmlSchemaType(name = "anyURI")
    protected List<String> analyticsModuleContentSchemaLocation;

    @XmlElement(name = "AnalyticsModuleDescription")
    protected List<ConfigDescription> analyticsModuleDescription;

    
    @Getter @XmlElement(name = "Extension")
    protected SupportedAnalyticsModulesExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<String> getAnalyticsModuleContentSchemaLocation() {
        if (analyticsModuleContentSchemaLocation == null) {
            analyticsModuleContentSchemaLocation = new ArrayList<String>();
        }
        return this.analyticsModuleContentSchemaLocation;
    }

    
    public List<ConfigDescription> getAnalyticsModuleDescription() {
        if (analyticsModuleDescription == null) {
            analyticsModuleDescription = new ArrayList<ConfigDescription>();
        }
        return this.analyticsModuleDescription;
    }

    
    public void setExtension(SupportedAnalyticsModulesExtension value) {
        this.extension = value;
    }

}
