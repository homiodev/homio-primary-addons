package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MetadataStreamExtension",
        propOrder = {"any", "audioAnalyticsStream", "extension"})
public class MetadataStreamExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlElement(name = "AudioAnalyticsStream")
    protected AudioAnalyticsStream audioAnalyticsStream;


    @Getter @XmlElement(name = "Extension")
    protected MetadataStreamExtension2 extension;


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setAudioAnalyticsStream(AudioAnalyticsStream value) {
        this.audioAnalyticsStream = value;
    }


    public void setExtension(MetadataStreamExtension2 value) {
        this.extension = value;
    }
}
