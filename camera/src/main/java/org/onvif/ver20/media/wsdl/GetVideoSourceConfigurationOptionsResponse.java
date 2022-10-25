package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.VideoSourceConfigurationOptions;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Options" type="{http://www.onvif.org/ver10/schema}VideoSourceConfigurationOptions"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "options"
})
@XmlRootElement(name = "GetVideoSourceConfigurationOptionsResponse")
public class GetVideoSourceConfigurationOptionsResponse {

  @XmlElement(name = "Options", required = true)
  protected VideoSourceConfigurationOptions options;

  /**
   * Ruft den Wert der options-Eigenschaft ab.
   *
   * @return possible object is {@link VideoSourceConfigurationOptions }
   */
  public VideoSourceConfigurationOptions getOptions() {
    return options;
  }

  /**
   * Legt den Wert der options-Eigenschaft fest.
   *
   * @param value allowed object is {@link VideoSourceConfigurationOptions }
   */
  public void setOptions(VideoSourceConfigurationOptions value) {
    this.options = value;
  }

}
