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
        name = "ReceiverConfiguration",
        propOrder = {"mode", "mediaUri", "streamSetup", "any"})
public class ReceiverConfiguration {


    @XmlElement(name = "Mode", required = true)
    protected ReceiverMode mode;


    @Getter @XmlElement(name = "MediaUri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String mediaUri;


    @Getter @XmlElement(name = "StreamSetup", required = true)
    protected StreamSetup streamSetup;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setMode(ReceiverMode value) {
        this.mode = value;
    }


    public void setMediaUri(String value) {
        this.mediaUri = value;
    }


    public void setStreamSetup(StreamSetup value) {
        this.streamSetup = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
