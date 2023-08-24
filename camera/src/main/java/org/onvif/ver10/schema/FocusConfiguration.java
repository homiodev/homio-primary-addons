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
        name = "FocusConfiguration",
        propOrder = {"autoFocusMode", "defaultSpeed", "nearLimit", "farLimit", "any"})
public class FocusConfiguration {


    @Getter @XmlElement(name = "AutoFocusMode", required = true)
    protected AutoFocusMode autoFocusMode;


    @Getter @XmlElement(name = "DefaultSpeed")
    protected float defaultSpeed;


    @Getter @XmlElement(name = "NearLimit")
    protected float nearLimit;


    @Getter @XmlElement(name = "FarLimit")
    protected float farLimit;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAutoFocusMode(AutoFocusMode value) {
        this.autoFocusMode = value;
    }


    public void setDefaultSpeed(float value) {
        this.defaultSpeed = value;
    }


    public void setNearLimit(float value) {
        this.nearLimit = value;
    }


    public void setFarLimit(float value) {
        this.farLimit = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
