package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZPresetTourStartingConditionOptions",
        propOrder = {"recurringTime", "recurringDuration", "direction", "extension"})
public class PTZPresetTourStartingConditionOptions {


    @XmlElement(name = "RecurringTime")
    protected IntRange recurringTime;


    @Getter @XmlElement(name = "RecurringDuration")
    protected DurationRange recurringDuration;

    @XmlElement(name = "Direction")
    protected List<PTZPresetTourDirection> direction;


    @Getter @XmlElement(name = "Extension")
    protected PTZPresetTourStartingConditionOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setRecurringTime(IntRange value) {
        this.recurringTime = value;
    }


    public void setRecurringDuration(DurationRange value) {
        this.recurringDuration = value;
    }


    public List<PTZPresetTourDirection> getDirection() {
        if (direction == null) {
            direction = new ArrayList<PTZPresetTourDirection>();
        }
        return this.direction;
    }


    public void setExtension(PTZPresetTourStartingConditionOptionsExtension value) {
        this.extension = value;
    }

}
