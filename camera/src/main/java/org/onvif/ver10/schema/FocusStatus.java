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
        name = "FocusStatus",
        propOrder = {"position", "moveStatus", "error", "any"})
public class FocusStatus {


    @Getter @XmlElement(name = "Position")
    protected float position;


    @Getter @XmlElement(name = "MoveStatus", required = true)
    protected MoveStatus moveStatus;


    @Getter @XmlElement(name = "Error", required = true)
    protected String error;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setPosition(float value) {
        this.position = value;
    }


    public void setMoveStatus(MoveStatus value) {
        this.moveStatus = value;
    }


    public void setError(String value) {
        this.error = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
