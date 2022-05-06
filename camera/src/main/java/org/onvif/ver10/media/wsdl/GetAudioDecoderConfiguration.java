package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "configurationToken" })
@XmlRootElement(name = "GetAudioDecoderConfiguration")
public class GetAudioDecoderConfiguration {

	@XmlElement(name = "ConfigurationToken", required = true)
	protected String configurationToken;
}
