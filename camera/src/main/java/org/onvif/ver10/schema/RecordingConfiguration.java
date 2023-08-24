package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RecordingConfiguration",
        propOrder = {"source", "content", "maximumRetentionTime", "any"})
public class RecordingConfiguration {


    @XmlElement(name = "Source", required = true)
    protected RecordingSourceInformation source;


    @Getter @XmlElement(name = "Content", required = true)
    protected String content;


    @Getter @XmlElement(name = "MaximumRetentionTime", required = true)
    protected Duration maximumRetentionTime;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setSource(RecordingSourceInformation value) {
        this.source = value;
    }


    public void setContent(String value) {
        this.content = value;
    }


    public void setMaximumRetentionTime(Duration value) {
        this.maximumRetentionTime = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
