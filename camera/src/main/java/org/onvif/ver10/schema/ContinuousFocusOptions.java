package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ContinuousFocusOptions",
    propOrder = {"speed"})
public class ContinuousFocusOptions {

    @XmlElement(name = "Speed", required = true)
    protected FloatRange speed;

    /**
     * Ruft den Wert der speed-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    public FloatRange getSpeed() {
        return speed;
    }

    /**
     * Legt den Wert der speed-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setSpeed(FloatRange value) {
        this.speed = value;
    }
}
