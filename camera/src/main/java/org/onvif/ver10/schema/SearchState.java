package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r SearchState.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="SearchState">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Queued"/>
 *     <enumeration value="Searching"/>
 *     <enumeration value="Completed"/>
 *     <enumeration value="Unknown"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "SearchState")
@XmlEnum
public enum SearchState {

  /**
   * The search is queued and not yet started.
   */
  @XmlEnumValue("Queued")
  QUEUED("Queued"),

  /**
   * The search is underway and not yet completed.
   */
  @XmlEnumValue("Searching")
  SEARCHING("Searching"),

  /**
   * The search has been completed and no new results will be found.
   */
  @XmlEnumValue("Completed")
  COMPLETED("Completed"),

  /**
   * The state of the search is unknown. (This is not a valid response from GetSearchState.)
   */
  @XmlEnumValue("Unknown")
  UNKNOWN("Unknown");
  private final String value;

  SearchState(String v) {
    value = v;
  }

  public static SearchState fromValue(String v) {
    for (SearchState c : SearchState.values()) {
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
