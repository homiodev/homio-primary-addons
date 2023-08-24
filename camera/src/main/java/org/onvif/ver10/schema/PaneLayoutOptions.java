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
        name = "PaneLayoutOptions",
        propOrder = {"area", "extension"})
public class PaneLayoutOptions {

    @XmlElement(name = "Area", required = true)
    protected List<Rectangle> area;


    @Getter @XmlElement(name = "Extension")
    protected PaneOptionExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<Rectangle> getArea() {
        if (area == null) {
            area = new ArrayList<Rectangle>();
        }
        return this.area;
    }


    public void setExtension(PaneOptionExtension value) {
        this.extension = value;
    }

}
