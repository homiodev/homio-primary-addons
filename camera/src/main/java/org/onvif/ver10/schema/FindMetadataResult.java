package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import lombok.Getter;

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
    private final Map<QName, String> otherAttributes = new HashMap<>();

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
