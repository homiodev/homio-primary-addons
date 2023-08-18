package org.onvif.ver10.events.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Capabilities",
        propOrder = {"any"})
public class Capabilities {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlAttribute(name = "WSSubscriptionPolicySupport")
    protected Boolean wsSubscriptionPolicySupport;

    @XmlAttribute(name = "WSPullPointSupport")
    protected Boolean wsPullPointSupport;

    @XmlAttribute(name = "WSPausableSubscriptionManagerInterfaceSupport")
    protected Boolean wsPausableSubscriptionManagerInterfaceSupport;

    @XmlAttribute(name = "MaxNotificationProducers")
    protected Integer maxNotificationProducers;

    @XmlAttribute(name = "MaxPullPoints")
    protected Integer maxPullPoints;

    @XmlAttribute(name = "PersistentNotificationStorage")
    protected Boolean persistentNotificationStorage;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
