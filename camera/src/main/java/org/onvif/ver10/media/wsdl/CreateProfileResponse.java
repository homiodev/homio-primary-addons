package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.Profile;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "profile" })
@XmlRootElement(name = "CreateProfileResponse")
public class CreateProfileResponse {

	@XmlElement(name = "Profile", required = true)
	protected Profile profile;
}
