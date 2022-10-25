package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r ImageStabilizationMode.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="ImageStabilizationMode">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="OFF"/>
 *     <enumeration value="ON"/>
 *     <enumeration value="AUTO"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "ImageStabilizationMode")
@XmlEnum
public enum ImageStabilizationMode {

  OFF("OFF"), ON("ON"), AUTO("AUTO"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  ImageStabilizationMode(String v) {
    value = v;
  }

  public static ImageStabilizationMode fromValue(String v) {
    for (ImageStabilizationMode c : ImageStabilizationMode.values()) {
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
