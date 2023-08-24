package org.w3._2005._08.addressing;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.ToString;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MetadataType",
        propOrder = {"any"})
@ToString
public class MetadataType {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<>();

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
