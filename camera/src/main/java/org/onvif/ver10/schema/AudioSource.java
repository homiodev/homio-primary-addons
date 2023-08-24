package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AudioSource",
        propOrder = {"channels", "any"})
public class AudioSource extends DeviceEntity {

    @XmlElement(name = "Channels")
    protected int channels;

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @Getter @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return any;
    }

}
