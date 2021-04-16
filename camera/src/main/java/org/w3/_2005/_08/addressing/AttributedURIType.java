package org.w3._2005._08.addressing;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributedURIType", propOrder = {"value"})
public class AttributedURIType {

    @XmlValue
    @XmlSchemaType(name = "anyURI")
    protected String value;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();
}
