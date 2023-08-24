package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDColorOptions",
        propOrder = {"color", "transparent", "extension"})
public class OSDColorOptions {


    @XmlElement(name = "Color")
    protected ColorOptions color;


    @XmlElement(name = "Transparent")
    protected IntRange transparent;


    @XmlElement(name = "Extension")
    protected OSDColorOptionsExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setColor(ColorOptions value) {
        this.color = value;
    }


    public void setTransparent(IntRange value) {
        this.transparent = value;
    }


    public void setExtension(OSDColorOptionsExtension value) {
        this.extension = value;
    }

}
