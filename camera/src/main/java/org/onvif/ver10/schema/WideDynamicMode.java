package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r WideDynamicMode.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="WideDynamicMode">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="OFF"/>
 *     <enumeration value="ON"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "WideDynamicMode")
@XmlEnum
public enum WideDynamicMode {

  OFF, ON;

  public static WideDynamicMode fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
