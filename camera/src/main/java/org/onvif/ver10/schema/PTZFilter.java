package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZFilter",
        propOrder = {"status", "position"})
public class PTZFilter {


    @XmlElement(name = "Status")
    protected boolean status;


    @XmlElement(name = "Position")
    protected boolean position;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setStatus(boolean value) {
        this.status = value;
    }


    public void setPosition(boolean value) {
        this.position = value;
    }

}
