package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZPresetTourStartingCondition",
        propOrder = {"recurringTime", "recurringDuration", "direction", "extension"})
public class PTZPresetTourStartingCondition {


    @XmlElement(name = "RecurringTime")
    protected Integer recurringTime;


    @XmlElement(name = "RecurringDuration")
    protected Duration recurringDuration;


    @XmlElement(name = "Direction")
    protected PTZPresetTourDirection direction;


    @XmlElement(name = "Extension")
    protected PTZPresetTourStartingConditionExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setRecurringTime(Integer value) {
        this.recurringTime = value;
    }


    public void setRecurringDuration(Duration value) {
        this.recurringDuration = value;
    }


    public void setDirection(PTZPresetTourDirection value) {
        this.direction = value;
    }


    public void setExtension(PTZPresetTourStartingConditionExtension value) {
        this.extension = value;
    }

}
