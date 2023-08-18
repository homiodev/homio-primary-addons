package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"includeCapability"})
@XmlRootElement(name = "GetServices")
public class GetServices {

    @XmlElement(name = "IncludeCapability")
    protected boolean includeCapability;
}
