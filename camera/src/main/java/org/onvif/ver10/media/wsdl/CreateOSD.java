package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.OSDConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osd", "any"})
@XmlRootElement(name = "CreateOSD")
public class CreateOSD {

    @XmlElement(name = "OSD", required = true)
    protected OSDConfiguration osd;

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
