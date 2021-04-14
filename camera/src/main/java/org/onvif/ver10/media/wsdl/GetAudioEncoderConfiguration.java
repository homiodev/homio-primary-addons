package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "configurationToken" })
@XmlRootElement(name = "GetAudioEncoderConfiguration")
public class GetAudioEncoderConfiguration {

	@XmlElement(name = "ConfigurationToken", required = true)
	protected String configurationToken;
}
