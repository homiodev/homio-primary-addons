package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FocusStatus20",
        propOrder = {"position", "moveStatus", "error", "extension"})
public class FocusStatus20 {


    @XmlElement(name = "Position")
    protected float position;


    @XmlElement(name = "MoveStatus", required = true)
    protected MoveStatus moveStatus;


    @XmlElement(name = "Error")
    protected String error;


    @XmlElement(name = "Extension")
    protected FocusStatus20Extension extension;


    @XmlAnyAttribute
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


    public void setExtension(FocusStatus20Extension value) {
        this.extension = value;
    }

}
