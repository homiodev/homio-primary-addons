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
        name = "RuleEngineConfiguration",
        propOrder = {"rule", "extension"})
public class RuleEngineConfiguration {

    @XmlElement(name = "Rule")
    protected List<Config> rule;

    
    @Getter @XmlElement(name = "Extension")
    protected RuleEngineConfigurationExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<Config> getRule() {
        if (rule == null) {
            rule = new ArrayList<Config>();
        }
        return this.rule;
    }

    
    public void setExtension(RuleEngineConfigurationExtension value) {
        this.extension = value;
    }

}
