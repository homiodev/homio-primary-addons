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


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZStatus",
        propOrder = {"position", "moveStatus", "error", "utcTime", "any"})
public class PTZStatus {


    @XmlElement(name = "Position")
    protected PTZVector position;


    @Getter @XmlElement(name = "MoveStatus")
    protected PTZMoveStatus moveStatus;


    @Getter @XmlElement(name = "Error")
    protected String error;


    @Getter @XmlElement(name = "UtcTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar utcTime;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setPosition(PTZVector value) {
        this.position = value;
    }


    public void setMoveStatus(PTZMoveStatus value) {
        this.moveStatus = value;
    }


    public void setError(String value) {
        this.error = value;
    }


    public void setUtcTime(XMLGregorianCalendar value) {
        this.utcTime = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
