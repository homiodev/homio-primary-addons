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
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AudioEncoderConfiguration", propOrder = {"encoding", "bitrate", "sampleRate", "multicast", "sessionTimeout", "any"})
public class AudioEncoderConfiguration extends ConfigurationEntity {

  @XmlElement(name = "Encoding", required = true)
  protected AudioEncoding encoding;
  @XmlElement(name = "Bitrate")
  protected int bitrate;
  @XmlElement(name = "SampleRate")
  protected int sampleRate;
  @XmlElement(name = "Multicast", required = true)
  protected MulticastConfiguration multicast;
  @XmlElement(name = "SessionTimeout", required = true)
  protected Duration sessionTimeout;
  @XmlAnyElement(lax = true)
  protected List<java.lang.Object> any;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

  /**
   * Ruft den Wert der encoding-Eigenschaft ab.
   *
   * @return possible object is {@link AudioEncoding }
   */
  public AudioEncoding getEncoding() {
    return encoding;
  }

  /**
   * Legt den Wert der encoding-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioEncoding }
   */
  public void setEncoding(AudioEncoding value) {
    this.encoding = value;
  }

  /**
   * Ruft den Wert der bitrate-Eigenschaft ab.
   */
  public int getBitrate() {
    return bitrate;
  }

  /**
   * Legt den Wert der bitrate-Eigenschaft fest.
   */
  public void setBitrate(int value) {
    this.bitrate = value;
  }

  /**
   * Ruft den Wert der sampleRate-Eigenschaft ab.
   */
  public int getSampleRate() {
    return sampleRate;
  }

  /**
   * Legt den Wert der sampleRate-Eigenschaft fest.
   */
  public void setSampleRate(int value) {
    this.sampleRate = value;
  }

  /**
   * Ruft den Wert der multicast-Eigenschaft ab.
   *
   * @return possible object is {@link MulticastConfiguration }
   */
  public MulticastConfiguration getMulticast() {
    return multicast;
  }

  /**
   * Legt den Wert der multicast-Eigenschaft fest.
   *
   * @param value allowed object is {@link MulticastConfiguration }
   */
  public void setMulticast(MulticastConfiguration value) {
    this.multicast = value;
  }

  /**
   * Ruft den Wert der sessionTimeout-Eigenschaft ab.
   *
   * @return possible object is {@link Duration }
   */
  public Duration getSessionTimeout() {
    return sessionTimeout;
  }

  /**
   * Legt den Wert der sessionTimeout-Eigenschaft fest.
   *
   * @param value allowed object is {@link Duration }
   */
  public void setSessionTimeout(Duration value) {
    this.sessionTimeout = value;
  }

  /**
   * Gets the value of the any property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the any property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getAny().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link Element } {@link java.lang.Object }
   */
  public List<java.lang.Object> getAny() {
    if (any == null) {
      any = new ArrayList<java.lang.Object>();
    }
    return this.any;
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
