package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r TrackType.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="TrackType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Video"/>
 *     <enumeration value="Audio"/>
 *     <enumeration value="Metadata"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "TrackType")
@XmlEnum
public enum TrackType {

  @XmlEnumValue("Video")
  VIDEO("Video"), @XmlEnumValue("Audio")
  AUDIO("Audio"), @XmlEnumValue("Metadata")
  METADATA("Metadata"),

  /**
   * Placeholder for future extension.
   */
  @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  TrackType(String v) {
    value = v;
  }

  public static TrackType fromValue(String v) {
    for (TrackType c : TrackType.values()) {
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
