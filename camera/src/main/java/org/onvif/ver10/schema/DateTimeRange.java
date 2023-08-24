package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "DateTimeRange",
        propOrder = {"from", "until", "any"})
public class DateTimeRange {


    @Getter @XmlElement(name = "From", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar from;


    @Getter @XmlElement(name = "Until", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar until;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setFrom(XMLGregorianCalendar value) {
        this.from = value;
    }


    public void setUntil(XMLGregorianCalendar value) {
        this.until = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
