package org.onvif.ver10.events.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;
import org.oasis_open.docs.wsn.b_2.FilterType;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filter",
    "initialTerminationTime",
    "subscriptionPolicy",
    "any"
})
@XmlRootElement(name = "CreatePullPointSubscription")
public class CreatePullPointSubscription {

  @XmlElement(name = "Filter")
  protected FilterType filter;
  @XmlElementRef(name = "InitialTerminationTime", namespace = "http://www.onvif.org/ver10/events/wsdl", type = JAXBElement.class, required = false)
  protected JAXBElement<String> initialTerminationTime;
  @XmlElement(name = "SubscriptionPolicy")
  protected CreatePullPointSubscription.SubscriptionPolicy subscriptionPolicy;
  @XmlAnyElement(lax = true)
  protected List<Object> any;

  public CreatePullPointSubscription() {
    setInitialTerminationTime(new ObjectFactory().createCreatePullPointSubscriptionInitialTerminationTime("PT600S"));
  }

  public List<Object> getAny() {
    if (any == null) {
      any = new ArrayList<>();
    }
    return this.any;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {
      "any"
  })
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
