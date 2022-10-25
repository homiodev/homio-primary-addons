package org.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.AudioEncoder2Configuration;


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
 *         &lt;element name="Configuration" type="{http://www.onvif.org/ver10/schema}AudioEncoder2Configuration"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "configuration"
})
@XmlRootElement(name = "SetAudioEncoderConfiguration")
public class SetAudioEncoderConfiguration {

  @XmlElement(name = "Configuration", required = true)
  protected AudioEncoder2Configuration configuration;

  /**
   * Ruft den Wert der configuration-Eigenschaft ab.
   *
   * @return possible object is {@link AudioEncoder2Configuration }
   */
  public AudioEncoder2Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Legt den Wert der configuration-Eigenschaft fest.
   *
   * @param value allowed object is {@link AudioEncoder2Configuration }
   */
  public void setConfiguration(AudioEncoder2Configuration value) {
    this.configuration = value;
  }

}
