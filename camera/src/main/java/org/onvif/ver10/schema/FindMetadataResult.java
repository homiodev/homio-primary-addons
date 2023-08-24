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
        name = "FindMetadataResult",
        propOrder = {"recordingToken", "trackToken", "time", "any"})
public class FindMetadataResult {


    @XmlElement(name = "RecordingToken", required = true)
    protected String recordingToken;


    @Getter @XmlElement(name = "TrackToken", required = true)
    protected String trackToken;


    @Getter @XmlElement(name = "Time", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;

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


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
