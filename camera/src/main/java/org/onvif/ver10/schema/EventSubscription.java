package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.oasis_open.docs.wsn.b_2.FilterType;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "EventSubscription",
        propOrder = {"filter", "subscriptionPolicy", "any"})
public class EventSubscription {


    @Getter @XmlElement(name = "Filter")
    protected FilterType filter;


    @Getter @XmlElement(name = "SubscriptionPolicy")
    protected EventSubscription.SubscriptionPolicy subscriptionPolicy;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setFilter(FilterType value) {
        this.filter = value;
    }


    public void setSubscriptionPolicy(EventSubscription.SubscriptionPolicy value) {
        this.subscriptionPolicy = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"any"})
    public static class SubscriptionPolicy {

        @XmlAnyElement(lax = true)
        protected List<java.lang.Object> any;


        public List<java.lang.Object> getAny() {
            if (any == null) {
                any = new ArrayList<java.lang.Object>();
            }
            return this.any;
        }
    }
}
