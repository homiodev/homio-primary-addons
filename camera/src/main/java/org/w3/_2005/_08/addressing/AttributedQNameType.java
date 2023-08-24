







package org.w3._2005._08.addressing;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AttributedQNameType",
        propOrder = {"value"})
public class AttributedQNameType {


    @XmlValue
    protected QName value;

    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setValue(QName value) {
        this.value = value;
    }

}
