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
@XmlType(name = "Defogging", propOrder = {
    "mode",
    "level",
    "extension"
})
public class Defogging {

  @XmlElement(name = "Mode", required = true)
  protected String mode;
  @XmlElement(name = "Level")
  protected Float level;
  @XmlElement(name = "Extension")
  protected DefoggingExtension extension;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

  /**
   * Ruft den Wert der mode-Eigenschaft ab.
   *
   * @return possible object is {@link String }
   */
  public String getMode() {
    return mode;
  }

  /**
   * Legt den Wert der mode-Eigenschaft fest.
   *
   * @param value allowed object is {@link String }
   */
  public void setMode(String value) {
    this.mode = value;
  }

  /**
   * Ruft den Wert der level-Eigenschaft ab.
   *
   * @return possible object is {@link Float }
   */
  public Float getLevel() {
    return level;
  }

  /**
   * Legt den Wert der level-Eigenschaft fest.
   *
   * @param value allowed object is {@link Float }
   */
  public void setLevel(Float value) {
    this.level = value;
  }

  /**
   * Ruft den Wert der extension-Eigenschaft ab.
   *
   * @return possible object is {@link DefoggingExtension }
   */
  public DefoggingExtension getExtension() {
    return extension;
  }

  /**
   * Legt den Wert der extension-Eigenschaft fest.
   *
   * @param value allowed object is {@link DefoggingExtension }
   */
  public void setExtension(DefoggingExtension value) {
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
