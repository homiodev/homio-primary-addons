package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "profileToken" })
@XmlRootElement(name = "DeleteProfile")
public class DeleteProfile {

	@XmlElement(name = "ProfileToken", required = true)
	protected String profileToken;
}
