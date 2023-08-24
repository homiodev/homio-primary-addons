package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoEncoderOptionsExtension",
        propOrder = {"any", "jpeg", "mpeg4", "h264", "extension"})
public class VideoEncoderOptionsExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "JPEG")
    protected JpegOptions2 jpeg;

    @XmlElement(name = "MPEG4")
    protected Mpeg4Options2 mpeg4;

    
    @Getter @XmlElement(name = "H264")
    protected H264Options2 h264;

    
    @Getter @XmlElement(name = "Extension")
    protected VideoEncoderOptionsExtension2 extension;

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

    
    public JpegOptions2 getJPEG() {
        return jpeg;
    }

    
    public void setJPEG(JpegOptions2 value) {
        this.jpeg = value;
    }

    
    public Mpeg4Options2 getMPEG4() {
        return mpeg4;
    }

    
    public void setMPEG4(Mpeg4Options2 value) {
        this.mpeg4 = value;
    }

    
    public void setH264(H264Options2 value) {
        this.h264 = value;
    }

    
    public void setExtension(VideoEncoderOptionsExtension2 value) {
        this.extension = value;
    }
}
