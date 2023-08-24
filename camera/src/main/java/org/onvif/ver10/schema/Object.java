package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Object",
        propOrder = {"appearance", "behaviour", "extension"})
public class Object extends ObjectId {


    @XmlElement(name = "Appearance")
    protected Appearance appearance;


    @XmlElement(name = "Behaviour")
    protected Behaviour behaviour;


    @XmlElement(name = "Extension")
    protected ObjectExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAppearance(Appearance value) {
        this.appearance = value;
    }


    public void setBehaviour(Behaviour value) {
        this.behaviour = value;
    }


    public void setExtension(ObjectExtension value) {
        this.extension = value;
    }

}
