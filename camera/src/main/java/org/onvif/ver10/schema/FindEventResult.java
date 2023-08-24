package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;
import org.w3c.dom.Element;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FindEventResult",
        propOrder = {"recordingToken", "trackToken", "time", "event", "startStateEvent", "any"})
public class FindEventResult {


    @Getter @XmlElement(name = "RecordingToken", required = true)
    protected String recordingToken;


    @Getter @XmlElement(name = "TrackToken", required = true)
    protected String trackToken;


    @Getter @XmlElement(name = "Time", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;


    @Getter @XmlElement(name = "Event", required = true)
    protected NotificationMessageHolderType event;


    @Getter @XmlElement(name = "StartStateEvent")
    protected boolean startStateEvent;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setRecordingToken(String value) {
        this.recordingToken = value;
    }


    public void setTrackToken(String value) {
        this.trackToken = value;
    }


    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }


    public void setEvent(NotificationMessageHolderType value) {
        this.event = value;
    }


    public void setStartStateEvent(boolean value) {
        this.startStateEvent = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
