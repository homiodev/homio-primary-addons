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
        name = "PaneLayout",
        propOrder = {"pane", "area", "any"})
public class PaneLayout {


    @XmlElement(name = "Pane", required = true)
    protected String pane;


    @Getter @XmlElement(name = "Area", required = true)
    protected Rectangle area;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setPane(String value) {
        this.pane = value;
    }


    public void setArea(Rectangle value) {
        this.area = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
