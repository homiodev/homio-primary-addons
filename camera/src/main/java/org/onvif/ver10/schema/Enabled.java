package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r Enabled.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="Enabled">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="ENABLED"/>
 *     <enumeration value="DISABLED"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "Enabled")
@XmlEnum
public enum Enabled {

  ENABLED, DISABLED;

  public static Enabled fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
