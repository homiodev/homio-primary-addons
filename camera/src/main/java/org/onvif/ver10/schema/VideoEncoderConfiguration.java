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
        name = "VideoEncoderConfiguration",
        propOrder = {
                "encoding",
                "resolution",
                "quality",
                "rateControl",
                "mpeg4",
                "h264",
                "multicast",
                "sessionTimeout",
                "any"
        })
public class VideoEncoderConfiguration extends ConfigurationEntity {


    @XmlElement(name = "Encoding", required = true)
    protected VideoEncoding encoding;


    @Getter @XmlElement(name = "Resolution", required = true)
    protected VideoResolution resolution;


    @Getter @XmlElement(name = "Quality")
    protected float quality;


    @Getter @XmlElement(name = "RateControl")
    protected VideoRateControl rateControl;

    @XmlElement(name = "MPEG4")
    protected Mpeg4Configuration mpeg4;


    @Getter @XmlElement(name = "H264")
    protected H264Configuration h264;


    @Getter @XmlElement(name = "Multicast", required = true)
    protected MulticastConfiguration multicast;


    @Getter @XmlElement(name = "SessionTimeout", required = true)
    protected Duration sessionTimeout;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setEncoding(VideoEncoding value) {
        this.encoding = value;
    }


    public void setResolution(VideoResolution value) {
        this.resolution = value;
    }


    public void setQuality(float value) {
        this.quality = value;
    }


    public void setRateControl(VideoRateControl value) {
        this.rateControl = value;
    }


    public Mpeg4Configuration getMPEG4() {
        return mpeg4;
    }


    public void setMPEG4(Mpeg4Configuration value) {
        this.mpeg4 = value;
    }


    public void setH264(H264Configuration value) {
        this.h264 = value;
    }


    public void setMulticast(MulticastConfiguration value) {
        this.multicast = value;
    }


    public void setSessionTimeout(Duration value) {
        this.sessionTimeout = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
