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
        name = "RecordingCapabilities",
        propOrder = {
                "xAddr",
                "receiverSource",
                "mediaProfileSource",
                "dynamicRecordings",
                "dynamicTracks",
                "maxStringLength",
                "any"
        })
public class RecordingCapabilities {

    @XmlElement(name = "XAddr", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String xAddr;


    @Getter @XmlElement(name = "ReceiverSource")
    protected boolean receiverSource;


    @Getter @XmlElement(name = "MediaProfileSource")
    protected boolean mediaProfileSource;


    @Getter @XmlElement(name = "DynamicRecordings")
    protected boolean dynamicRecordings;


    @Getter @XmlElement(name = "DynamicTracks")
    protected boolean dynamicTracks;


    @Getter @XmlElement(name = "MaxStringLength")
    protected int maxStringLength;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public String getXAddr() {
        return xAddr;
    }


    public void setXAddr(String value) {
        this.xAddr = value;
    }


    public void setReceiverSource(boolean value) {
        this.receiverSource = value;
    }


    public void setMediaProfileSource(boolean value) {
        this.mediaProfileSource = value;
    }


    public void setDynamicRecordings(boolean value) {
        this.dynamicRecordings = value;
    }


    public void setDynamicTracks(boolean value) {
        this.dynamicTracks = value;
    }


    public void setMaxStringLength(int value) {
        this.maxStringLength = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
