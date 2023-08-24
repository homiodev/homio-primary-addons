package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "LensProjection",
        propOrder = {"angle", "radius", "transmittance", "any"})
public class LensProjection {


    @XmlElement(name = "Angle")
    protected float angle;


    @Getter @XmlElement(name = "Radius")
    protected float radius;


    @Getter @XmlElement(name = "Transmittance")
    protected Float transmittance;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAngle(float value) {
        this.angle = value;
    }


    public void setRadius(float value) {
        this.radius = value;
    }


    public void setTransmittance(Float value) {
        this.transmittance = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
