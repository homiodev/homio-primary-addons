package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ShapeDescriptor",
        propOrder = {"boundingBox", "centerOfGravity", "polygon", "extension"})
public class ShapeDescriptor {


    @Getter @XmlElement(name = "BoundingBox", required = true)
    protected Rectangle boundingBox;


    @Getter @XmlElement(name = "CenterOfGravity", required = true)
    protected Vector centerOfGravity;

    @XmlElement(name = "Polygon")
    protected List<Polygon> polygon;


    @Getter @XmlElement(name = "Extension")
    protected ShapeDescriptorExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setBoundingBox(Rectangle value) {
        this.boundingBox = value;
    }


    public void setCenterOfGravity(Vector value) {
        this.centerOfGravity = value;
    }


    public List<Polygon> getPolygon() {
        if (polygon == null) {
            polygon = new ArrayList<Polygon>();
        }
        return this.polygon;
    }


    public void setExtension(ShapeDescriptorExtension value) {
        this.extension = value;
    }

}
