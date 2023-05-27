package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r PropertyOperation.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <p>
 *
 * <pre>
 * <simpleType name="PropertyOperation">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Initialized"/>
 *     <enumeration value="Deleted"/>
 *     <enumeration value="Changed"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "PropertyOperation")
@XmlEnum
public enum PropertyOperation {
    @XmlEnumValue("Initialized")
    INITIALIZED("Initialized"),
    @XmlEnumValue("Deleted")
    DELETED("Deleted"),
    @XmlEnumValue("Changed")
    CHANGED("Changed");
    private final String value;

    PropertyOperation(String v) {
        value = v;
    }

    public static PropertyOperation fromValue(String v) {
        for (PropertyOperation c : PropertyOperation.values()) {
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
