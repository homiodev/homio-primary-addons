package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AnalyticsDeviceCapabilities",
        propOrder = {"xAddr", "ruleSupport", "extension"})
public class AnalyticsDeviceCapabilities {

    @XmlElement(name = "XAddr", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String xAddr;

    @XmlElement(name = "RuleSupport")
    protected Boolean ruleSupport;


    @Getter @XmlElement(name = "Extension")
    protected AnalyticsDeviceExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public String getXAddr() {
        return xAddr;
    }


    public void setXAddr(String value) {
        this.xAddr = value;
    }


    public Boolean isRuleSupport() {
        return ruleSupport;
    }


    public void setRuleSupport(Boolean value) {
        this.ruleSupport = value;
    }


    public void setExtension(AnalyticsDeviceExtension value) {
        this.extension = value;
    }

}
