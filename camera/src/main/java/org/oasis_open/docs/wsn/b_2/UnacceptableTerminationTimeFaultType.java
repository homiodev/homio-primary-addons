package org.oasis_open.docs.wsn.b_2;

import lombok.Getter;
import lombok.Setter;
import org.oasis_open.docs.wsrf.bf_2.BaseFaultType;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnacceptableTerminationTimeFaultType", propOrder = { "minimumTime", "maximumTime" })
public class UnacceptableTerminationTimeFaultType extends BaseFaultType {

	@XmlElement(name = "MinimumTime", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar minimumTime;
	@XmlElement(name = "MaximumTime")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar maximumTime;
}
