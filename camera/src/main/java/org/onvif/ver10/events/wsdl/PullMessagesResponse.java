package org.onvif.ver10.events.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "currentTime",
    "terminationTime",
    "notificationMessage"
})
@XmlRootElement(name = "PullMessagesResponse")
@ToString
public class PullMessagesResponse {

  @XmlElement(name = "CurrentTime", required = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar currentTime;
  @XmlElement(name = "TerminationTime", required = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar terminationTime;
  @XmlElement(name = "NotificationMessage", namespace = "http://docs.oasis-open.org/wsn/b-2")
  protected List<NotificationMessageHolderType> notificationMessage;

  public List<NotificationMessageHolderType> getNotificationMessage() {
    if (notificationMessage == null) {
      notificationMessage = new ArrayList<>();
    }
    return this.notificationMessage;
  }
}
