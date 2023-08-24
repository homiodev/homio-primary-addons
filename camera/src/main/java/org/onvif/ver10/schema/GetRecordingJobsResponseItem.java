package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "GetRecordingJobsResponseItem",
        propOrder = {"jobToken", "jobConfiguration", "any"})
public class GetRecordingJobsResponseItem {


    @XmlElement(name = "JobToken", required = true)
    protected String jobToken;


    @Getter @XmlElement(name = "JobConfiguration", required = true)
    protected RecordingJobConfiguration jobConfiguration;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setJobToken(String value) {
        this.jobToken = value;
    }


    public void setJobConfiguration(RecordingJobConfiguration value) {
        this.jobConfiguration = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
