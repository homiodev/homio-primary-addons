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
        name = "ReceiverStateInformation",
        propOrder = {"state", "autoCreated", "any"})
public class ReceiverStateInformation {


    @Getter @XmlElement(name = "State", required = true)
    protected ReceiverState state;


    @Getter @XmlElement(name = "AutoCreated")
    protected boolean autoCreated;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setState(ReceiverState value) {
        this.state = value;
    }


    public void setAutoCreated(boolean value) {
        this.autoCreated = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
