package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r PTZPresetTourDirection.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="PTZPresetTourDirection">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Forward"/>
 *     <enumeration value="Backward"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "PTZPresetTourDirection")
@XmlEnum
public enum PTZPresetTourDirection {

  @XmlEnumValue("Forward")
  FORWARD("Forward"), @XmlEnumValue("Backward")
  BACKWARD("Backward"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  PTZPresetTourDirection(String v) {
    value = v;
  }

  public static PTZPresetTourDirection fromValue(String v) {
    for (PTZPresetTourDirection c : PTZPresetTourDirection.values()) {
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
