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
@XmlType(name = "AudioClassDescriptor", propOrder = {"classCandidate", "extension"})
public class AudioClassDescriptor {

  @XmlElement(name = "ClassCandidate")
  protected List<AudioClassCandidate> classCandidate;
  @XmlElement(name = "Extension")
  protected AudioClassDescriptorExtension extension;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

  /**
   * Gets the value of the classCandidate property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the classCandidate property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getClassCandidate().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link AudioClassCandidate }
   */
  public List<AudioClassCandidate> getClassCandidate() {
    if (classCandidate == null) {
      classCandidate = new ArrayList<AudioClassCandidate>();
    }
    return this.classCandidate;
  }

  /**
   * Ruft den Wert der extension-Eigenschaft ab.
   *
   * @return possible object is {@link AudioClassDescriptorExtension }
   */
  public AudioClassDescriptorExtension getExtension() {
    return extension;
  }

  /**
   * Legt den Wert der extension-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioClassDescriptorExtension }
   */
  public void setExtension(AudioClassDescriptorExtension value) {
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
