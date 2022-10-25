package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r Mpeg4Profile.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="Mpeg4Profile">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="SP"/>
 *     <enumeration value="ASP"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "Mpeg4Profile")
@XmlEnum
public enum Mpeg4Profile {

  SP, ASP;

  public static Mpeg4Profile fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
