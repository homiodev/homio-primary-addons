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
        name = "JpegDecOptions",
        propOrder = {"resolutionsAvailable", "supportedInputBitrate", "supportedFrameRate", "any"})
public class JpegDecOptions {

    @XmlElement(name = "ResolutionsAvailable", required = true)
    protected List<VideoResolution> resolutionsAvailable;


    @Getter @XmlElement(name = "SupportedInputBitrate", required = true)
    protected IntRange supportedInputBitrate;


    @Getter @XmlElement(name = "SupportedFrameRate", required = true)
    protected IntRange supportedFrameRate;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<VideoResolution> getResolutionsAvailable() {
        if (resolutionsAvailable == null) {
            resolutionsAvailable = new ArrayList<VideoResolution>();
        }
        return this.resolutionsAvailable;
    }


    public void setSupportedInputBitrate(IntRange value) {
        this.supportedInputBitrate = value;
    }


    public void setSupportedFrameRate(IntRange value) {
        this.supportedFrameRate = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
