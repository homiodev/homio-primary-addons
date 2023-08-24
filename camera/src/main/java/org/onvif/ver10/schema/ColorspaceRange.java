package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ColorspaceRange",
        propOrder = {"x", "y", "z", "colorspace"})
public class ColorspaceRange {

    
    @XmlElement(name = "X", required = true)
    protected FloatRange x;

    
    @XmlElement(name = "Y", required = true)
    protected FloatRange y;

    
    @XmlElement(name = "Z", required = true)
    protected FloatRange z;

    
    @XmlElement(name = "Colorspace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String colorspace;

    
    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setX(FloatRange value) {
        this.x = value;
    }

    
    public void setY(FloatRange value) {
        this.y = value;
    }

    
    public void setZ(FloatRange value) {
        this.z = value;
    }

    
    public void setColorspace(String value) {
        this.colorspace = value;
    }

}
