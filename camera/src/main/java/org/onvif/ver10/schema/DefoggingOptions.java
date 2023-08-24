package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "DefoggingOptions",
        propOrder = {"mode", "level", "any"})
public class DefoggingOptions {

    @XmlElement(name = "Mode", required = true)
    protected List<String> mode;


    @Getter @XmlElement(name = "Level")
    protected boolean level;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<String> getMode() {
        if (mode == null) {
            mode = new ArrayList<String>();
        }
        return this.mode;
    }


    public void setLevel(boolean value) {
        this.level = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
