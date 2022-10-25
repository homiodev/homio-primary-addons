package org.onvif.ver10.events.wsdl;

import de.onvif.soap.SOAP;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "timeout",
    "messageLimit",
    "any"
})
@XmlRootElement(name = "PullMessages")
public class PullMessages {

  @XmlElement(name = "Timeout", required = true)
  protected Duration timeout = SOAP.DATATYPE_FACTORY.newDuration("PT8S");
  @XmlElement(name = "MessageLimit")
  protected int messageLimit = 1;
  @XmlAnyElement(lax = true)
  protected List<Object> any;

  public List<Object> getAny() {
    if (any == null) {
      any = new ArrayList<>();
    }
    return this.any;
  }
}
