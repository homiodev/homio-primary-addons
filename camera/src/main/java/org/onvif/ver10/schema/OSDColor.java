package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDColor",
        propOrder = {"color"})
public class OSDColor {


    @XmlElement(name = "Color", required = true)
    protected Color color;


    @XmlAttribute(name = "Transparent")
    protected Integer transparent;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setColor(Color value) {
        this.color = value;
    }


    public void setTransparent(Integer value) {
        this.transparent = value;
    }

}
