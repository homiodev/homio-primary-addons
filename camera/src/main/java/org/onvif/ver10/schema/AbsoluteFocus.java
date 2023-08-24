package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AbsoluteFocus",
        propOrder = {"position", "speed"})
public class AbsoluteFocus {

    
    @XmlElement(name = "Position")
    protected float position;

    
    @XmlElement(name = "Speed")
    protected Float speed;

    
    public void setPosition(float value) {
        this.position = value;
    }

    
    public void setSpeed(Float value) {
        this.speed = value;
    }
}
