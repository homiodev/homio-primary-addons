package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZConfigurationOptions",
        propOrder = {"spaces", "ptzTimeout", "any", "ptControlDirection", "extension"})
public class PTZConfigurationOptions {


    @XmlElement(name = "Spaces", required = true)
    protected PTZSpaces spaces;

    @XmlElement(name = "PTZTimeout", required = true)
    protected DurationRange ptzTimeout;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "PTControlDirection")
    protected PTControlDirectionOptions ptControlDirection;


    @Getter @XmlElement(name = "Extension")
    protected PTZConfigurationOptions2 extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setSpaces(PTZSpaces value) {
        this.spaces = value;
    }


    public DurationRange getPTZTimeout() {
        return ptzTimeout;
    }


    public void setPTZTimeout(DurationRange value) {
        this.ptzTimeout = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public PTControlDirectionOptions getPTControlDirection() {
        return ptControlDirection;
    }


    public void setPTControlDirection(PTControlDirectionOptions value) {
        this.ptControlDirection = value;
    }


    public void setExtension(PTZConfigurationOptions2 value) {
        this.extension = value;
    }

}
