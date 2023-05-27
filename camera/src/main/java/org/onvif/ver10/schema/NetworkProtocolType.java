package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r NetworkProtocolType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <p>
 *
 * <pre>
 * <simpleType name="NetworkProtocolType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="HTTP"/>
 *     <enumeration value="HTTPS"/>
 *     <enumeration value="RTSP"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "NetworkProtocolType")
@XmlEnum
public enum NetworkProtocolType {
    HTTP,
    HTTPS,
    RTSP;

    public static NetworkProtocolType fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }
}
