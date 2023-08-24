package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AnalyticsEngine",
        propOrder = {"analyticsEngineConfiguration", "any"})
public class AnalyticsEngine extends ConfigurationEntity {

    @XmlElement(name = "AnalyticsEngineConfiguration", required = true)
    protected AnalyticsDeviceEngineConfiguration analyticsEngineConfiguration;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

     @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAnalyticsEngineConfiguration(AnalyticsDeviceEngineConfiguration value) {
        this.analyticsEngineConfiguration = value;
    }

    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
