package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AnalyticsStateInformation",
        propOrder = {"analyticsEngineControlToken", "state", "any"})
public class AnalyticsStateInformation {

    
    @Getter @XmlElement(name = "AnalyticsEngineControlToken", required = true)
    protected String analyticsEngineControlToken;

    
    @Getter @XmlElement(name = "State", required = true)
    protected AnalyticsState state;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setAnalyticsEngineControlToken(String value) {
        this.analyticsEngineControlToken = value;
    }

    
    public void setState(AnalyticsState value) {
        this.state = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
