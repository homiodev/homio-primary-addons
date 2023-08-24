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
        name = "StreamSetup",
        propOrder = {"stream", "transport", "any"})
public class StreamSetup {


    @Getter @XmlElement(name = "Stream", required = true)
    protected StreamType stream;


    @Getter @XmlElement(name = "Transport", required = true)
    protected Transport transport;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setStream(StreamType value) {
        this.stream = value;
    }


    public void setTransport(Transport value) {
        this.transport = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
