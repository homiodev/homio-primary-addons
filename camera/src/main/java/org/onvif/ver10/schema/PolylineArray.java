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
        name = "PolylineArray",
        propOrder = {"segment", "extension"})
public class PolylineArray {

    @XmlElement(name = "Segment", required = true)
    protected List<Polyline> segment;


    @Getter @XmlElement(name = "Extension")
    protected PolylineArrayExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<Polyline> getSegment() {
        if (segment == null) {
            segment = new ArrayList<Polyline>();
        }
        return this.segment;
    }


    public void setExtension(PolylineArrayExtension value) {
        this.extension = value;
    }

}
