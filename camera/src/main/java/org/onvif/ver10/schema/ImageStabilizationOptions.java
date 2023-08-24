package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ImageStabilizationOptions",
        propOrder = {"mode", "level", "extension"})
public class ImageStabilizationOptions {

    @XmlElement(name = "Mode", required = true)
    protected List<ImageStabilizationMode> mode;


    @Getter @XmlElement(name = "Level")
    protected FloatRange level;


    @Getter @XmlElement(name = "Extension")
    protected ImageStabilizationOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<ImageStabilizationMode> getMode() {
        if (mode == null) {
            mode = new ArrayList<ImageStabilizationMode>();
        }
        return this.mode;
    }


    public void setLevel(FloatRange value) {
        this.level = value;
    }


    public void setExtension(ImageStabilizationOptionsExtension value) {
        this.extension = value;
    }

}
