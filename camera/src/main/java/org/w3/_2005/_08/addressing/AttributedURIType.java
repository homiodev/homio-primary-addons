package org.w3._2005._08.addressing;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AttributedURIType",
        propOrder = {"value"})
@ToString
public class AttributedURIType {

    @XmlValue
    @XmlSchemaType(name = "anyURI")
    protected String value;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();
}
