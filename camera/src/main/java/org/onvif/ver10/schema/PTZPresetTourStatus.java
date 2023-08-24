package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZPresetTourStatus",
        propOrder = {"state", "currentTourSpot", "extension"})
public class PTZPresetTourStatus {


    @XmlElement(name = "State", required = true)
    protected PTZPresetTourState state;


    @XmlElement(name = "CurrentTourSpot")
    protected PTZPresetTourSpot currentTourSpot;


    @XmlElement(name = "Extension")
    protected PTZPresetTourStatusExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setState(PTZPresetTourState value) {
        this.state = value;
    }


    public void setCurrentTourSpot(PTZPresetTourSpot value) {
        this.currentTourSpot = value;
    }


    public void setExtension(PTZPresetTourStatusExtension value) {
        this.extension = value;
    }

}
