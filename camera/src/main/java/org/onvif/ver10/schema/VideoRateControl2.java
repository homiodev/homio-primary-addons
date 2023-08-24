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
        name = "VideoRateControl2",
        propOrder = {"frameRateLimit", "bitrateLimit", "any"})
public class VideoRateControl2 {


    @Getter @XmlElement(name = "FrameRateLimit")
    protected float frameRateLimit;


    @Getter @XmlElement(name = "BitrateLimit")
    protected int bitrateLimit;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlAttribute(name = "ConstantBitRate")
    protected Boolean constantBitRate;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setFrameRateLimit(float value) {
        this.frameRateLimit = value;
    }


    public void setBitrateLimit(int value) {
        this.bitrateLimit = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public Boolean isConstantBitRate() {
        return constantBitRate;
    }


    public void setConstantBitRate(Boolean value) {
        this.constantBitRate = value;
    }

}
