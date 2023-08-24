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
        name = "PTZPositionFilter",
        propOrder = {"minPosition", "maxPosition", "enterOrExit", "any"})
public class PTZPositionFilter {


    @XmlElement(name = "MinPosition", required = true)
    protected PTZVector minPosition;


    @Getter @XmlElement(name = "MaxPosition", required = true)
    protected PTZVector maxPosition;


    @Getter @XmlElement(name = "EnterOrExit")
    protected boolean enterOrExit;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setMinPosition(PTZVector value) {
        this.minPosition = value;
    }


    public void setMaxPosition(PTZVector value) {
        this.maxPosition = value;
    }


    public void setEnterOrExit(boolean value) {
        this.enterOrExit = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
