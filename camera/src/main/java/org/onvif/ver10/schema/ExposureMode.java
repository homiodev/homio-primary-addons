package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r ExposureMode.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="ExposureMode">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="AUTO"/>
 *     <enumeration value="MANUAL"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "ExposureMode")
@XmlEnum
public enum ExposureMode {

  AUTO, MANUAL;

  public static ExposureMode fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
