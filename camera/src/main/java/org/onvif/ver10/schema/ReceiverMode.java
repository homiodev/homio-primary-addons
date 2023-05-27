package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r ReceiverMode.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <p>
 *
 * <pre>
 * <simpleType name="ReceiverMode">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="AutoConnect"/>
 *     <enumeration value="AlwaysConnect"/>
 *     <enumeration value="NeverConnect"/>
 *     <enumeration value="Unknown"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "ReceiverMode")
@XmlEnum
public enum ReceiverMode {

    /**
     * The receiver connects on demand, as required by consumers of the media streams.
     */
    @XmlEnumValue("AutoConnect")
    AUTO_CONNECT("AutoConnect"),

    /**
     * The receiver attempts to maintain a persistent connection to the configured endpoint.
     */
    @XmlEnumValue("AlwaysConnect")
    ALWAYS_CONNECT("AlwaysConnect"),

    /**
     * The receiver does not attempt to connect.
     */
    @XmlEnumValue("NeverConnect")
    NEVER_CONNECT("NeverConnect"),

    /**
     * This case should never happen.
     */
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    ReceiverMode(String v) {
        value = v;
    }

    public static ReceiverMode fromValue(String v) {
        for (ReceiverMode c : ReceiverMode.values()) {
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
