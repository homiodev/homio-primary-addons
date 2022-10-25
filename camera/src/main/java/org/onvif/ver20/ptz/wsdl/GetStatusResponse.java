package org.onvif.ver20.ptz.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.PTZStatus;

/**
 * <p>
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PTZStatus" type="{http://www.onvif.org/ver10/schema}PTZStatus"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"ptzStatus"})
@XmlRootElement(name = "GetStatusResponse")
public class GetStatusResponse {

  @XmlElement(name = "PTZStatus", required = true)
  protected PTZStatus ptzStatus;

  /**
   * Ruft den Wert der ptzStatus-Eigenschaft ab.
   *
   * @return possible object is {@link PTZStatus }
   */
  public PTZStatus getPTZStatus() {
    return ptzStatus;
  }

  /**
   * Legt den Wert der ptzStatus-Eigenschaft fest.
   *
   * @param value allowed object is {@link PTZStatus }
   */
  public void setPTZStatus(PTZStatus value) {
    this.ptzStatus = value;
  }

}
