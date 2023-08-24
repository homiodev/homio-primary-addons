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
        name = "PTZPresetTourOptions",
        propOrder = {"autoStart", "startingCondition", "tourSpot", "any"})
public class PTZPresetTourOptions {


    @Getter @XmlElement(name = "AutoStart")
    protected boolean autoStart;


    @Getter @XmlElement(name = "StartingCondition", required = true)
    protected PTZPresetTourStartingConditionOptions startingCondition;


    @Getter @XmlElement(name = "TourSpot", required = true)
    protected PTZPresetTourSpotOptions tourSpot;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAutoStart(boolean value) {
        this.autoStart = value;
    }


    public void setStartingCondition(PTZPresetTourStartingConditionOptions value) {
        this.startingCondition = value;
    }


    public void setTourSpot(PTZPresetTourSpotOptions value) {
        this.tourSpot = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
