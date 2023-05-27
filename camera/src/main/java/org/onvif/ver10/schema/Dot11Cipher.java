package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r Dot11Cipher.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <p>
 *
 * <pre>
 * <simpleType name="Dot11Cipher">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="CCMP"/>
 *     <enumeration value="TKIP"/>
 *     <enumeration value="Any"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "Dot11Cipher")
@XmlEnum
public enum Dot11Cipher {
    CCMP("CCMP"),
    TKIP("TKIP"),
    @XmlEnumValue("Any")
    ANY("Any"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    Dot11Cipher(String v) {
        value = v;
    }

    public static Dot11Cipher fromValue(String v) {
        for (Dot11Cipher c : Dot11Cipher.values()) {
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
