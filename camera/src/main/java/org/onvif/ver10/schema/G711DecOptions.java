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
        name = "G711DecOptions",
        propOrder = {"bitrate", "sampleRateRange", "any"})
public class G711DecOptions {


    @XmlElement(name = "Bitrate", required = true)
    protected IntList bitrate;


    @Getter @XmlElement(name = "SampleRateRange", required = true)
    protected IntList sampleRateRange;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setBitrate(IntList value) {
        this.bitrate = value;
    }


    public void setSampleRateRange(IntList value) {
        this.sampleRateRange = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
