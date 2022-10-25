package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AudioSource", propOrder = {"channels", "any"})
public class AudioSource extends DeviceEntity {

  @XmlElement(name = "Channels")
  protected int channels;

  @XmlAnyElement(lax = true)
  protected List<Object> any;

  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  public List<Object> getAny() {
    if (any == null) {
      any = new ArrayList<>();
    }
    return any;
  }

  public Map<QName, String> getOtherAttributes() {
    return otherAttributes;
  }

}
