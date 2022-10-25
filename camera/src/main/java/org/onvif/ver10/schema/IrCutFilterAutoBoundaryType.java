package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r IrCutFilterAutoBoundaryType.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="IrCutFilterAutoBoundaryType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Common"/>
 *     <enumeration value="ToOn"/>
 *     <enumeration value="ToOff"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "IrCutFilterAutoBoundaryType")
@XmlEnum
public enum IrCutFilterAutoBoundaryType {

  @XmlEnumValue("Common")
  COMMON("Common"), @XmlEnumValue("ToOn")
  TO_ON("ToOn"), @XmlEnumValue("ToOff")
  TO_OFF("ToOff"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  IrCutFilterAutoBoundaryType(String v) {
    value = v;
  }

  public static IrCutFilterAutoBoundaryType fromValue(String v) {
    for (IrCutFilterAutoBoundaryType c : IrCutFilterAutoBoundaryType.values()) {
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
