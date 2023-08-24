package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ImagingSettingsExtension20",
        propOrder = {"any", "imageStabilization", "extension"})
public class ImagingSettingsExtension20 {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlElement(name = "ImageStabilization")
    protected ImageStabilization imageStabilization;


    @Getter @XmlElement(name = "Extension")
    protected ImagingSettingsExtension202 extension;


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setImageStabilization(ImageStabilization value) {
        this.imageStabilization = value;
    }


    public void setExtension(ImagingSettingsExtension202 value) {
        this.extension = value;
    }
}
