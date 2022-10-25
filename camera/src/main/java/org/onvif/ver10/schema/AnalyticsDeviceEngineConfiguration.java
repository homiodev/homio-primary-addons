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
@XmlType(name = "AnalyticsDeviceEngineConfiguration", propOrder = {"engineConfiguration", "extension"})
public class AnalyticsDeviceEngineConfiguration {

  @XmlElement(name = "EngineConfiguration", required = true)
  protected List<EngineConfiguration> engineConfiguration;
  @XmlElement(name = "Extension")
  protected AnalyticsDeviceEngineConfigurationExtension extension;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

  /**
   * Gets the value of the engineConfiguration property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the engineConfiguration property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getEngineConfiguration().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link EngineConfiguration }
   */
  public List<EngineConfiguration> getEngineConfiguration() {
    if (engineConfiguration == null) {
      engineConfiguration = new ArrayList<EngineConfiguration>();
    }
    return this.engineConfiguration;
  }

  /**
   * Ruft den Wert der extension-Eigenschaft ab.
   *
   * @return possible object is {@link AnalyticsDeviceEngineConfigurationExtension }
   */
  public AnalyticsDeviceEngineConfigurationExtension getExtension() {
    return extension;
  }

  /**
   * Legt den Wert der extension-Eigenschaft fest.
   *
   * @param value allowed object is {@link AnalyticsDeviceEngineConfigurationExtension }
   */
  public void setExtension(AnalyticsDeviceEngineConfigurationExtension value) {
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
