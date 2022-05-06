package org.onvif.ver10.events.wsdl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "capabilities"
})
@XmlRootElement(name = "GetServiceCapabilitiesResponse")
public class GetServiceCapabilitiesResponse {

    @XmlElement(name = "Capabilities", required = true)
    protected Capabilities capabilities;
}
