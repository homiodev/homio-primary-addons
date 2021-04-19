package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DefoggingMode")
@XmlEnum
public enum DefoggingMode {

    OFF,
    ON,
    AUTO;

    public String value() {
        return name();
    }

    public static DefoggingMode fromValue(String v) {
        return valueOf(v);
    }

}
