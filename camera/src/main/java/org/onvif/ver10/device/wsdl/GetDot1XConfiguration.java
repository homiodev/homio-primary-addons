package org.onvif.ver10.device.wsdl;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "dot1XConfigurationToken" })
@XmlRootElement(name = "GetDot1XConfiguration")
public class GetDot1XConfiguration {

	@XmlElement(name = "Dot1XConfigurationToken", required = true)
	protected String dot1XConfigurationToken;
}
