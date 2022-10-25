package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r IPAddressFilterType.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="IPAddressFilterType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Allow"/>
 *     <enumeration value="Deny"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "IPAddressFilterType")
@XmlEnum
public enum IPAddressFilterType {

  @XmlEnumValue("Allow")
  ALLOW("Allow"), @XmlEnumValue("Deny")
  DENY("Deny");
  private final String value;

  IPAddressFilterType(String v) {
    value = v;
  }

  public static IPAddressFilterType fromValue(String v) {
    for (IPAddressFilterType c : IPAddressFilterType.values()) {
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
