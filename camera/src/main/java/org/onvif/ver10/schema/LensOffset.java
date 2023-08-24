package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LensOffset")
public class LensOffset {


    @XmlAttribute(name = "x")
    protected Float x;


    @XmlAttribute(name = "y")
    protected Float y;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setX(Float value) {
        this.x = value;
    }


    public void setY(Float value) {
        this.y = value;
    }

}
