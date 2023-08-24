package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FrameExtension",
        propOrder = {"any", "motionInCells", "extension"})
public class FrameExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlElement(name = "MotionInCells")
    protected MotionInCells motionInCells;


    @Getter @XmlElement(name = "Extension")
    protected FrameExtension2 extension;


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setMotionInCells(MotionInCells value) {
        this.motionInCells = value;
    }


    public void setExtension(FrameExtension2 value) {
        this.extension = value;
    }
}
