package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r PTZMoveStatus complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="PTZMoveStatus">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PanTilt" type="{http://www.onvif.org/ver10/schema}MoveStatus" minOccurs="0"/>
 *         <element name="Zoom" type="{http://www.onvif.org/ver10/schema}MoveStatus" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PTZMoveStatus", propOrder = {"panTilt", "zoom"})
public class PTZMoveStatus {

  @XmlElement(name = "PanTilt")
  protected MoveStatus panTilt;
  @XmlElement(name = "Zoom")
  protected MoveStatus zoom;

  /**
   * Ruft den Wert der panTilt-Eigenschaft ab.
   *
   * @return possible object is {@link MoveStatus }
   */
  public MoveStatus getPanTilt() {
    return panTilt;
  }

  /**
   * Legt den Wert der panTilt-Eigenschaft fest.
   *
   * @param value allowed object is {@link MoveStatus }
   */
  public void setPanTilt(MoveStatus value) {
    this.panTilt = value;
  }

  /**
   * Ruft den Wert der zoom-Eigenschaft ab.
   *
   * @return possible object is {@link MoveStatus }
   */
  public MoveStatus getZoom() {
    return zoom;
  }

  /**
   * Legt den Wert der zoom-Eigenschaft fest.
   *
   * @param value allowed object is {@link MoveStatus }
   */
  public void setZoom(MoveStatus value) {
    this.zoom = value;
  }

}
