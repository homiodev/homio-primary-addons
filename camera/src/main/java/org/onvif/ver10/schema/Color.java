package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Color")
public class Color {

    
    @XmlAttribute(name = "X", required = true)
    protected float x;

    
    @XmlAttribute(name = "Y", required = true)
    protected float y;

    
    @XmlAttribute(name = "Z", required = true)
    protected float z;

    
    @XmlAttribute(name = "Colorspace")
    @XmlSchemaType(name = "anyURI")
    protected String colorspace;

    
    public void setX(float value) {
        this.x = value;
    }

    
    public void setY(float value) {
        this.y = value;
    }

    
    public void setZ(float value) {
        this.z = value;
    }

    
    public void setColorspace(String value) {
        this.colorspace = value;
    }
}
