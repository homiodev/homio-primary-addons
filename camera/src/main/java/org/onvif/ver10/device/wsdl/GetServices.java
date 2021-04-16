package org.onvif.ver10.device.wsdl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"includeCapability"})
@XmlRootElement(name = "GetServices")
public class GetServices {

    @XmlElement(name = "IncludeCapability")
    protected boolean includeCapability;
}
