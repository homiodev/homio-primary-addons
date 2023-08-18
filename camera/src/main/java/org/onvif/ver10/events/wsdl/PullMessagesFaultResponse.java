package org.onvif.ver10.events.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.xml.datatype.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"maxTimeout", "maxMessageLimit", "any"})
@XmlRootElement(name = "PullMessagesFaultResponse")
public class PullMessagesFaultResponse {

    @XmlElement(name = "MaxTimeout", required = true)
    protected Duration maxTimeout;

    @XmlElement(name = "MaxMessageLimit")
    protected int maxMessageLimit;

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
