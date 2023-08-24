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
        name = "PTZPresetTourSpotOptions",
        propOrder = {"presetDetail", "stayTime", "any"})
public class PTZPresetTourSpotOptions {


    @Getter @XmlElement(name = "PresetDetail", required = true)
    protected PTZPresetTourPresetDetailOptions presetDetail;


    @Getter @XmlElement(name = "StayTime", required = true)
    protected DurationRange stayTime;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setPresetDetail(PTZPresetTourPresetDetailOptions value) {
        this.presetDetail = value;
    }


    public void setStayTime(DurationRange value) {
        this.stayTime = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
