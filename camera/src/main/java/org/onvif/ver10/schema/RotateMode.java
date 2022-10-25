package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r RotateMode.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="RotateMode">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="OFF"/>
 *     <enumeration value="ON"/>
 *     <enumeration value="AUTO"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "RotateMode")
@XmlEnum
public enum RotateMode {

  OFF, ON, AUTO;

  public static RotateMode fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
