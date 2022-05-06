package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.AudioDecoderConfigurationOptions;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "options" })
@XmlRootElement(name = "GetAudioDecoderConfigurationOptionsResponse")
public class GetAudioDecoderConfigurationOptionsResponse {

	@XmlElement(name = "Options", required = true)
	protected AudioDecoderConfigurationOptions options;
}
