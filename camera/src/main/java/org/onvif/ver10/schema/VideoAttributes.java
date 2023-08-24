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
        name = "VideoAttributes",
        propOrder = {"bitrate", "width", "height", "encoding", "framerate", "any"})
public class VideoAttributes {


    @Getter @XmlElement(name = "Bitrate")
    protected Integer bitrate;


    @Getter @XmlElement(name = "Width")
    protected int width;


    @Getter @XmlElement(name = "Height")
    protected int height;


    @Getter @XmlElement(name = "Encoding", required = true)
    protected VideoEncoding encoding;


    @Getter @XmlElement(name = "Framerate")
    protected float framerate;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setBitrate(Integer value) {
        this.bitrate = value;
    }


    public void setWidth(int value) {
        this.width = value;
    }


    public void setHeight(int value) {
        this.height = value;
    }


    public void setEncoding(VideoEncoding value) {
        this.encoding = value;
    }


    public void setFramerate(float value) {
        this.framerate = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
