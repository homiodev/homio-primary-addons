package org.onvif.ver10.schema;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AudioDecoderConfigurationOptions", propOrder = {"aacDecOptions", "g711DecOptions", "g726DecOptions", "extension"})
public class AudioDecoderConfigurationOptions {

  @XmlElement(name = "AACDecOptions")
  protected AACDecOptions aacDecOptions;
  @XmlElement(name = "G711DecOptions")
  protected G711DecOptions g711DecOptions;
  @XmlElement(name = "G726DecOptions")
  protected G726DecOptions g726DecOptions;
  @XmlElement(name = "Extension")
  protected AudioDecoderConfigurationOptionsExtension extension;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

  /**
   * Ruft den Wert der aacDecOptions-Eigenschaft ab.
   *
   * @return possible object is {@link AACDecOptions }
   */
  public AACDecOptions getAACDecOptions() {
    return aacDecOptions;
  }

  /**
   * Legt den Wert der aacDecOptions-Eigenschaft fest.
   *
   * @param value allowed object is {@link AACDecOptions }
   */
  public void setAACDecOptions(AACDecOptions value) {
    this.aacDecOptions = value;
  }

  /**
   * Ruft den Wert der g711DecOptions-Eigenschaft ab.
   *
   * @return possible object is {@link G711DecOptions }
   */
  public G711DecOptions getG711DecOptions() {
    return g711DecOptions;
  }

  /**
   * Legt den Wert der g711DecOptions-Eigenschaft fest.
   *
   * @param value allowed object is {@link G711DecOptions }
   */
  public void setG711DecOptions(G711DecOptions value) {
    this.g711DecOptions = value;
  }

  /**
   * Ruft den Wert der g726DecOptions-Eigenschaft ab.
   *
   * @return possible object is {@link G726DecOptions }
   */
  public G726DecOptions getG726DecOptions() {
    return g726DecOptions;
  }

  /**
   * Legt den Wert der g726DecOptions-Eigenschaft fest.
   *
   * @param value allowed object is {@link G726DecOptions }
   */
  public void setG726DecOptions(G726DecOptions value) {
    this.g726DecOptions = value;
  }

  /**
   * Ruft den Wert der extension-Eigenschaft ab.
   *
   * @return possible object is {@link AudioDecoderConfigurationOptionsExtension }
   */
  public AudioDecoderConfigurationOptionsExtension getExtension() {
    return extension;
  }

  /**
   * Legt den Wert der extension-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioDecoderConfigurationOptionsExtension }
   */
  public void setExtension(AudioDecoderConfigurationOptionsExtension value) {
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
