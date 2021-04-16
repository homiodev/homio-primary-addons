package org.oasis_open.docs.wsn.b_2;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.w3._2005._08.addressing.EndpointReferenceType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"consumerReference", "filter", "initialTerminationTime", "subscriptionPolicy", "any"})
@XmlRootElement(name = "Subscribe")
public class Subscribe {

    @XmlElement(name = "ConsumerReference", required = true)
    protected EndpointReferenceType consumerReference;
    @XmlElement(name = "Filter")
    protected FilterType filter;
    @XmlElementRef(name = "InitialTerminationTime", namespace = "http://docs.oasis-open.org/wsn/b-2", type = JAXBElement.class, required = false)
    protected JAXBElement<String> initialTerminationTime;
    @XmlElement(name = "SubscriptionPolicy")
    protected Subscribe.SubscriptionPolicy subscriptionPolicy;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"any"})
    public static class SubscriptionPolicy {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<>();
            }
            return this.any;
        }
    }
}
