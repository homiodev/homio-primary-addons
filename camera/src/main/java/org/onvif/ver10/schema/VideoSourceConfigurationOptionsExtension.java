package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoSourceConfigurationOptionsExtension",
        propOrder = {"any", "rotate", "extension"})
public class VideoSourceConfigurationOptionsExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlElement(name = "Rotate")
    protected RotateOptions rotate;


    @Getter @XmlElement(name = "Extension")
    protected VideoSourceConfigurationOptionsExtension2 extension;


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setRotate(RotateOptions value) {
        this.rotate = value;
    }


    public void setExtension(VideoSourceConfigurationOptionsExtension2 value) {
        this.extension = value;
    }
}
