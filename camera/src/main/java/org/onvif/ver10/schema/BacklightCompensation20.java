package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "BacklightCompensation20",
        propOrder = {"mode", "level"})
public class BacklightCompensation20 {

    @XmlElement(name = "Mode", required = true)
    protected BacklightCompensationMode mode;

    @XmlElement(name = "Level")
    protected Float level;

    /**
     * Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link BacklightCompensationMode }
     */
    public BacklightCompensationMode getMode() {
        return mode;
    }

    /**
     * Legt den Wert der mode-Eigenschaft fest.
     *
     * @param value allowed object is {@link BacklightCompensationMode }
     */
    public void setMode(BacklightCompensationMode value) {
        this.mode = value;
    }

    /**
     * Ruft den Wert der level-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    public Float getLevel() {
        return level;
    }

    /**
     * Legt den Wert der level-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setLevel(Float value) {
        this.level = value;
    }
}
