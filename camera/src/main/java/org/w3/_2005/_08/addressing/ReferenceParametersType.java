package org.w3._2005._08.addressing;

import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceParametersType", propOrder = {"any"})
@ToString
public class ReferenceParametersType {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
