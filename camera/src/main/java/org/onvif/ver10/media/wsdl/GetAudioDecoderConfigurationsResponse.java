package org.onvif.ver10.media.wsdl;

import java.util.ArrayList;
import java.util.List;
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
@XmlType(name = "", propOrder = {"configurations"})
@XmlRootElement(name = "GetAudioDecoderConfigurationsResponse")
public class GetAudioDecoderConfigurationsResponse {

  @XmlElement(name = "Configurations")
  protected List<AudioDecoderConfiguration> configurations;

  public List<AudioDecoderConfiguration> getConfigurations() {
    if (configurations == null) {
      configurations = new ArrayList<>();
    }
    return this.configurations;
  }
}
