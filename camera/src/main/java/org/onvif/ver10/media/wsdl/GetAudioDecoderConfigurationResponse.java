package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "configuration" })
@XmlRootElement(name = "GetAudioDecoderConfigurationResponse")
public class GetAudioDecoderConfigurationResponse {

	@XmlElement(name = "Configuration", required = true)
	protected AudioDecoderConfiguration configuration;
}
