package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RecordingJobStateTrack",
        propOrder = {"sourceTag", "destination", "error", "state", "any"})
public class RecordingJobStateTrack {


    @Getter @XmlElement(name = "SourceTag", required = true)
    protected String sourceTag;


    @Getter @XmlElement(name = "Destination", required = true)
    protected String destination;


    @Getter @XmlElement(name = "Error")
    protected String error;


    @Getter @XmlElement(name = "State", required = true)
    protected String state;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setSourceTag(String value) {
        this.sourceTag = value;
    }


    public void setDestination(String value) {
        this.destination = value;
    }


    public void setError(String value) {
        this.error = value;
    }


    public void setState(String value) {
        this.state = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
