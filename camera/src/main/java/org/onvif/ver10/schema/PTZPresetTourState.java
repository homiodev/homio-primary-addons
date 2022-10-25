package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r PTZPresetTourState.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="PTZPresetTourState">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Idle"/>
 *     <enumeration value="Touring"/>
 *     <enumeration value="Paused"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "PTZPresetTourState")
@XmlEnum
public enum PTZPresetTourState {

  @XmlEnumValue("Idle")
  IDLE("Idle"), @XmlEnumValue("Touring")
  TOURING("Touring"), @XmlEnumValue("Paused")
  PAUSED("Paused"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  PTZPresetTourState(String v) {
    value = v;
  }

  public static PTZPresetTourState fromValue(String v) {
    for (PTZPresetTourState c : PTZPresetTourState.values()) {
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
