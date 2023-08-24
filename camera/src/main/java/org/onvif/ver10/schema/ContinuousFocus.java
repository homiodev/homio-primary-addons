package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ContinuousFocus",
        propOrder = {"speed"})
public class ContinuousFocus {

    /**
     * -- GETTER --
     *  Ruft den Wert der speed-Eigenschaft ab.
     */
    @XmlElement(name = "Speed")
    protected float speed;

    /**
     * Legt den Wert der speed-Eigenschaft fest.
     */
    public void setSpeed(float value) {
        this.speed = value;
    }
}
