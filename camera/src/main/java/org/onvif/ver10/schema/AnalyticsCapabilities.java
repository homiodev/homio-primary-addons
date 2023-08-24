package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AnalyticsCapabilities",
        propOrder = {"xAddr", "ruleSupport", "analyticsModuleSupport", "any"})
public class AnalyticsCapabilities {

    @XmlElement(name = "XAddr", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String xAddr;


    @Getter @XmlElement(name = "RuleSupport")
    protected boolean ruleSupport;


    @Getter @XmlElement(name = "AnalyticsModuleSupport")
    protected boolean analyticsModuleSupport;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<>();


    public String getXAddr() {
        return xAddr;
    }


    public void setXAddr(String value) {
        this.xAddr = value;
    }


    public void setRuleSupport(boolean value) {
        this.ruleSupport = value;
    }


    public void setAnalyticsModuleSupport(boolean value) {
        this.analyticsModuleSupport = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
