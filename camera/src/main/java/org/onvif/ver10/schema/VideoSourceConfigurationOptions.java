package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoSourceConfigurationOptions",
        propOrder = {"boundsRange", "videoSourceTokensAvailable", "extension"})
public class VideoSourceConfigurationOptions {


    @XmlElement(name = "BoundsRange", required = true)
    protected IntRectangleRange boundsRange;

    @XmlElement(name = "VideoSourceTokensAvailable", required = true)
    protected List<String> videoSourceTokensAvailable;


    @Getter @XmlElement(name = "Extension")
    protected VideoSourceConfigurationOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setBoundsRange(IntRectangleRange value) {
        this.boundsRange = value;
    }


    public List<String> getVideoSourceTokensAvailable() {
        if (videoSourceTokensAvailable == null) {
            videoSourceTokensAvailable = new ArrayList<String>();
        }
        return this.videoSourceTokensAvailable;
    }


    public void setExtension(VideoSourceConfigurationOptionsExtension value) {
        this.extension = value;
    }

}
