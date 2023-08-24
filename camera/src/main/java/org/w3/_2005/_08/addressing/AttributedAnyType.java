







package org.w3._2005._08.addressing;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AttributedAnyType",
        propOrder = {"any"})
public class AttributedAnyType {


    @XmlAnyElement(lax = true)
    protected Object any;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAny(Object value) {
        this.any = value;
    }

}
