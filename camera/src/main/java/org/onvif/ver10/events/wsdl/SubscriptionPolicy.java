package org.onvif.ver10.events.wsdl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionPolicy", propOrder = {
    "any"
})
public class SubscriptionPolicy {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "ChangedOnly")
    protected Boolean changedOnly;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
