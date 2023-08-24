package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ZoomLimits",
        propOrder = {"range"})
public class ZoomLimits {

    @XmlElement(name = "Range", required = true)
    protected Space1DDescription range;

    public void setRange(Space1DDescription value) {
        this.range = value;
    }
}
