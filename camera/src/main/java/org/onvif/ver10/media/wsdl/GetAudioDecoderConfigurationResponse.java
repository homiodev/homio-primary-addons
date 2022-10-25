package org.onvif.ver10.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"configuration"})
@XmlRootElement(name = "GetAudioDecoderConfigurationResponse")
public class GetAudioDecoderConfigurationResponse {

  @XmlElement(name = "Configuration", required = true)
  protected AudioDecoderConfiguration configuration;
}
