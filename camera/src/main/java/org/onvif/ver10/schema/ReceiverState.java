package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r ReceiverState.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="ReceiverState">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="NotConnected"/>
 *     <enumeration value="Connecting"/>
 *     <enumeration value="Connected"/>
 *     <enumeration value="Unknown"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "ReceiverState")
@XmlEnum
public enum ReceiverState {

  /**
   * The receiver is not connected.
   */
  @XmlEnumValue("NotConnected")
  NOT_CONNECTED("NotConnected"),

  /**
   * The receiver is attempting to connect.
   */
  @XmlEnumValue("Connecting")
  CONNECTING("Connecting"),

  /**
   * The receiver is connected.
   */
  @XmlEnumValue("Connected")
  CONNECTED("Connected"),

  /**
   * This case should never happen.
   */
  @XmlEnumValue("Unknown")
  UNKNOWN("Unknown");
  private final String value;

  ReceiverState(String v) {
    value = v;
  }

  public static ReceiverState fromValue(String v) {
    for (ReceiverState c : ReceiverState.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }

  public String value() {
    return value;
  }

}
