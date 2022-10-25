package org.oasis_open.docs.wsn.b_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import lombok.Setter;
import org.oasis_open.docs.wsrf.bf_2.BaseFaultType;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnacceptableInitialTerminationTimeFaultType", propOrder = {"minimumTime", "maximumTime"})
public class UnacceptableInitialTerminationTimeFaultType extends BaseFaultType {

  @XmlElement(name = "MinimumTime", required = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar minimumTime;
  @XmlElement(name = "MaximumTime")
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar maximumTime;
}
