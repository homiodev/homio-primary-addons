package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Transformation",
        propOrder = {"translate", "scale", "extension"})
public class Transformation {

    @XmlElement(name = "Translate")
    protected Vector translate;

    @XmlElement(name = "Scale")
    protected Vector scale;

    @XmlElement(name = "Extension")
    protected TransformationExtension extension;

    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<>();
}
