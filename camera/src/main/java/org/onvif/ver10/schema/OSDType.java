package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r OSDType.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="OSDType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Text"/>
 *     <enumeration value="Image"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "OSDType")
@XmlEnum
public enum OSDType {

  @XmlEnumValue("Text")
  TEXT("Text"), @XmlEnumValue("Image")
  IMAGE("Image"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  OSDType(String v) {
    value = v;
  }

  public static OSDType fromValue(String v) {
    for (OSDType c : OSDType.values()) {
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
