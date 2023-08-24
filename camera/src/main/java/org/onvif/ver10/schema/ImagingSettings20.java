package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ImagingSettings20",
        propOrder = {
                "backlightCompensation",
                "brightness",
                "colorSaturation",
                "contrast",
                "exposure",
                "focus",
                "irCutFilter",
                "sharpness",
                "wideDynamicRange",
                "whiteBalance",
                "extension"
        })
public class ImagingSettings20 {


    @XmlElement(name = "BacklightCompensation")
    protected BacklightCompensation20 backlightCompensation;


    @XmlElement(name = "Brightness")
    protected Float brightness;


    @XmlElement(name = "ColorSaturation")
    protected Float colorSaturation;


    @XmlElement(name = "Contrast")
    protected Float contrast;


    @XmlElement(name = "Exposure")
    protected Exposure20 exposure;


    @XmlElement(name = "Focus")
    protected FocusConfiguration20 focus;


    @XmlElement(name = "IrCutFilter")
    protected IrCutFilterMode irCutFilter;


    @XmlElement(name = "Sharpness")
    protected Float sharpness;


    @XmlElement(name = "WideDynamicRange")
    protected WideDynamicRange20 wideDynamicRange;


    @XmlElement(name = "WhiteBalance")
    protected WhiteBalance20 whiteBalance;


    @XmlElement(name = "Extension")
    protected ImagingSettingsExtension20 extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setBacklightCompensation(BacklightCompensation20 value) {
        this.backlightCompensation = value;
    }


    public void setBrightness(Float value) {
        this.brightness = value;
    }


    public void setColorSaturation(Float value) {
        this.colorSaturation = value;
    }


    public void setContrast(Float value) {
        this.contrast = value;
    }


    public void setExposure(Exposure20 value) {
        this.exposure = value;
    }


    public void setFocus(FocusConfiguration20 value) {
        this.focus = value;
    }


    public void setIrCutFilter(IrCutFilterMode value) {
        this.irCutFilter = value;
    }


    public void setSharpness(Float value) {
        this.sharpness = value;
    }


    public void setWideDynamicRange(WideDynamicRange20 value) {
        this.wideDynamicRange = value;
    }


    public void setWhiteBalance(WhiteBalance20 value) {
        this.whiteBalance = value;
    }


    public void setExtension(ImagingSettingsExtension20 value) {
        this.extension = value;
    }

}
