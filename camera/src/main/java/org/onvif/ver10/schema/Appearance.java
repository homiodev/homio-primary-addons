package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Appearance",
        propOrder = {"transformation", "shape", "color", "clazz", "extension"})
public class Appearance {

    
    @XmlElement(name = "Transformation")
    protected Transformation transformation;

    
    @XmlElement(name = "Shape")
    protected ShapeDescriptor shape;

    
    @XmlElement(name = "Color")
    protected ColorDescriptor color;

    
    @XmlElement(name = "Class")
    protected ClassDescriptor clazz;

    
    @XmlElement(name = "Extension")
    protected AppearanceExtension extension;

    
    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setTransformation(Transformation value) {
        this.transformation = value;
    }

    
    public void setShape(ShapeDescriptor value) {
        this.shape = value;
    }

    
    public void setColor(ColorDescriptor value) {
        this.color = value;
    }

    
    public void setClazz(ClassDescriptor value) {
        this.clazz = value;
    }

    
    public void setExtension(AppearanceExtension value) {
        this.extension = value;
    }

}
