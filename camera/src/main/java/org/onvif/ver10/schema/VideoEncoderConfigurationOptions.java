package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoEncoderConfigurationOptions",
        propOrder = {"qualityRange", "jpeg", "mpeg4", "h264", "extension"})
public class VideoEncoderConfigurationOptions {


    @Getter @XmlElement(name = "QualityRange", required = true)
    protected IntRange qualityRange;

    @XmlElement(name = "JPEG")
    protected JpegOptions jpeg;

    @XmlElement(name = "MPEG4")
    protected Mpeg4Options mpeg4;


    @Getter @XmlElement(name = "H264")
    protected H264Options h264;


    @Getter @XmlElement(name = "Extension")
    protected VideoEncoderOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setQualityRange(IntRange value) {
        this.qualityRange = value;
    }


    public JpegOptions getJPEG() {
        return jpeg;
    }


    public void setJPEG(JpegOptions value) {
        this.jpeg = value;
    }


    public Mpeg4Options getMPEG4() {
        return mpeg4;
    }


    public void setMPEG4(Mpeg4Options value) {
        this.mpeg4 = value;
    }


    public void setH264(H264Options value) {
        this.h264 = value;
    }


    public void setExtension(VideoEncoderOptionsExtension value) {
        this.extension = value;
    }

}
