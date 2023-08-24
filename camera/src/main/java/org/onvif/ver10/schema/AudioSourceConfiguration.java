package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AudioSourceConfiguration",
        propOrder = {"sourceToken", "any"})
public class AudioSourceConfiguration extends ConfigurationEntity {

    @XmlElement(name = "SourceToken", required = true)
    protected String sourceToken;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

     @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<>();

    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
