package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AudioAnalyticsStream", propOrder = {"audioDescriptor", "extension"})
public class AudioAnalyticsStream {

  @XmlElement(name = "AudioDescriptor")
  protected List<AudioDescriptor> audioDescriptor;
  @XmlElement(name = "Extension")
  protected AudioAnalyticsStreamExtension extension;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

  /**
   * Gets the value of the audioDescriptor property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the audioDescriptor property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getAudioDescriptor().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link AudioDescriptor }
   */
  public List<AudioDescriptor> getAudioDescriptor() {
    if (audioDescriptor == null) {
      audioDescriptor = new ArrayList<AudioDescriptor>();
    }
    return this.audioDescriptor;
  }

  /**
   * Ruft den Wert der extension-Eigenschaft ab.
   *
   * @return possible object is {@link AudioAnalyticsStreamExtension }
   */
  public AudioAnalyticsStreamExtension getExtension() {
    return extension;
  }

  /**
   * Legt den Wert der extension-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioAnalyticsStreamExtension }
   */
  public void setExtension(AudioAnalyticsStreamExtension value) {
    this.extension = value;
  }

  /**
   * Gets a map that contains attributes that aren't bound to any typed property on this class.
   *
   * <p>
   * the map is keyed by the name of the attribute and the value is the string value of the attribute.
   * <p>
   * the map returned by this method is live, and you can add new attribute by updating the map directly. Because of this design, there's no setter.
   *
   * @return always non-null
   */
  public Map<QName, String> getOtherAttributes() {
    return otherAttributes;
  }

}
