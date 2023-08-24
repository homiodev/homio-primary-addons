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
        name = "AnalyticsEngineControl",
        propOrder = {
                "engineToken",
                "engineConfigToken",
                "inputToken",
                "receiverToken",
                "multicast",
                "subscription",
                "mode",
                "any"
        })
public class AnalyticsEngineControl extends ConfigurationEntity {

    
    @Getter @XmlElement(name = "EngineToken", required = true)
    protected String engineToken;

    
    @Getter @XmlElement(name = "EngineConfigToken", required = true)
    protected String engineConfigToken;

    @XmlElement(name = "InputToken", required = true)
    protected List<String> inputToken;

    @XmlElement(name = "ReceiverToken", required = true)
    protected List<String> receiverToken;

    
    @Getter @XmlElement(name = "Multicast")
    protected MulticastConfiguration multicast;

    
    @Getter @XmlElement(name = "Subscription", required = true)
    protected Config subscription;

    
    @Getter @XmlElement(name = "Mode", required = true)
    protected ModeOfOperation mode;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setEngineToken(String value) {
        this.engineToken = value;
    }

    
    public void setEngineConfigToken(String value) {
        this.engineConfigToken = value;
    }

    
    public List<String> getInputToken() {
        if (inputToken == null) {
            inputToken = new ArrayList<String>();
        }
        return this.inputToken;
    }

    
    public List<String> getReceiverToken() {
        if (receiverToken == null) {
            receiverToken = new ArrayList<String>();
        }
        return this.receiverToken;
    }

    
    public void setMulticast(MulticastConfiguration value) {
        this.multicast = value;
    }

    
    public void setSubscription(Config value) {
        this.subscription = value;
    }

    
    public void setMode(ModeOfOperation value) {
        this.mode = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
