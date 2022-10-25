package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r TransportProtocol.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="TransportProtocol">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="UDP"/>
 *     <enumeration value="TCP"/>
 *     <enumeration value="RTSP"/>
 *     <enumeration value="HTTP"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "TransportProtocol")
@XmlEnum
public enum TransportProtocol {

  UDP, TCP, RTSP, HTTP;

  public static TransportProtocol fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
