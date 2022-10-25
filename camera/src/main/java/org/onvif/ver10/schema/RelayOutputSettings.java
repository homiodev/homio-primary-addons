package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

/**
 * <p>
 * Java-Klasse f�r RelayOutputSettings complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="RelayOutputSettings">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}RelayMode"/>
 *         <element name="DelayTime" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         <element name="IdleState" type="{http://www.onvif.org/ver10/schema}RelayIdleState"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelayOutputSettings", propOrder = {"mode", "delayTime", "idleState"})
public class RelayOutputSettings {

  @XmlElement(name = "Mode", required = true)
  protected RelayMode mode;
  @XmlElement(name = "DelayTime", required = true)
  protected Duration delayTime;
  @XmlElement(name = "IdleState", required = true)
  protected RelayIdleState idleState;

  /**
   * Ruft den Wert der mode-Eigenschaft ab.
   *
   * @return possible object is {@link RelayMode }
   */
  public RelayMode getMode() {
    return mode;
  }

  /**
   * Legt den Wert der mode-Eigenschaft fest.
   *
   * @param value allowed object is {@link RelayMode }
   */
  public void setMode(RelayMode value) {
    this.mode = value;
  }

  /**
   * Ruft den Wert der delayTime-Eigenschaft ab.
   *
   * @return possible object is {@link Duration }
   */
  public Duration getDelayTime() {
    return delayTime;
  }

  /**
   * Legt den Wert der delayTime-Eigenschaft fest.
   *
   * @param value allowed object is {@link Duration }
   */
  public void setDelayTime(Duration value) {
    this.delayTime = value;
  }

  /**
   * Ruft den Wert der idleState-Eigenschaft ab.
   *
   * @return possible object is {@link RelayIdleState }
   */
  public RelayIdleState getIdleState() {
    return idleState;
  }

  /**
   * Legt den Wert der idleState-Eigenschaft fest.
   *
   * @param value allowed object is {@link RelayIdleState }
   */
  public void setIdleState(RelayIdleState value) {
    this.idleState = value;
  }

}
