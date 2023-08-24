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
        name = "MediaCapabilities",
        propOrder = {"xAddr", "streamingCapabilities", "any", "extension"})
public class MediaCapabilities {

    @XmlElement(name = "XAddr", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String xAddr;


    @Getter @XmlElement(name = "StreamingCapabilities", required = true)
    protected RealTimeStreamingCapabilities streamingCapabilities;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlElement(name = "Extension")
    protected MediaCapabilitiesExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public String getXAddr() {
        return xAddr;
    }


    public void setXAddr(String value) {
        this.xAddr = value;
    }


    public void setStreamingCapabilities(RealTimeStreamingCapabilities value) {
        this.streamingCapabilities = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setExtension(MediaCapabilitiesExtension value) {
        this.extension = value;
    }

}
