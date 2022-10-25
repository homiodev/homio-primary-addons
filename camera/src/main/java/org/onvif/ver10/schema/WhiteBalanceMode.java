package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r WhiteBalanceMode.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="WhiteBalanceMode">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="AUTO"/>
 *     <enumeration value="MANUAL"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "WhiteBalanceMode")
@XmlEnum
public enum WhiteBalanceMode {

  AUTO, MANUAL;

  public static WhiteBalanceMode fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
