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
        name = "SupportedRules",
        propOrder = {"ruleContentSchemaLocation", "ruleDescription", "extension"})
public class SupportedRules {

    @XmlElement(name = "RuleContentSchemaLocation")
    @XmlSchemaType(name = "anyURI")
    protected List<String> ruleContentSchemaLocation;

    @XmlElement(name = "RuleDescription")
    protected List<ConfigDescription> ruleDescription;


    @Getter @XmlElement(name = "Extension")
    protected SupportedRulesExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<String> getRuleContentSchemaLocation() {
        if (ruleContentSchemaLocation == null) {
            ruleContentSchemaLocation = new ArrayList<String>();
        }
        return this.ruleContentSchemaLocation;
    }


    public List<ConfigDescription> getRuleDescription() {
        if (ruleDescription == null) {
            ruleDescription = new ArrayList<ConfigDescription>();
        }
        return this.ruleDescription;
    }


    public void setExtension(SupportedRulesExtension value) {
        this.extension = value;
    }

}
