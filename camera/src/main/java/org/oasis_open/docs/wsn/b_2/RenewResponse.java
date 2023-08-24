







package org.oasis_open.docs.wsn.b_2;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"terminationTime", "currentTime", "any"})
@XmlRootElement(name = "RenewResponse")
public class RenewResponse {


    @XmlElement(name = "TerminationTime", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar terminationTime;


    @Getter @XmlElement(name = "CurrentTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar currentTime;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public void setTerminationTime(XMLGregorianCalendar value) {
        this.terminationTime = value;
    }


    public void setCurrentTime(XMLGregorianCalendar value) {
        this.currentTime = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
