package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r MoveStatus.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="MoveStatus">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="IDLE"/>
 *     <enumeration value="MOVING"/>
 *     <enumeration value="UNKNOWN"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "MoveStatus")
@XmlEnum
public enum MoveStatus {

  IDLE, MOVING, UNKNOWN;

  public static MoveStatus fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
