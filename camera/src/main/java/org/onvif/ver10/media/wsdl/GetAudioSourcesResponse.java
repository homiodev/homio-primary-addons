package org.onvif.ver10.media.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.AudioSource;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"audioSources"})
@XmlRootElement(name = "GetAudioSourcesResponse")
public class GetAudioSourcesResponse {

  @XmlElement(name = "AudioSources")
  protected List<AudioSource> audioSources;

  public List<AudioSource> getAudioSources() {
    if (audioSources == null) {
      audioSources = new ArrayList<>();
    }
    return this.audioSources;
  }

}
