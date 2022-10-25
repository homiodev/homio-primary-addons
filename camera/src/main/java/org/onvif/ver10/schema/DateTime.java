package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DateTime", propOrder = {"time", "date"})
public class DateTime {

  @XmlElement(name = "Time", required = true)
  protected Time time;
  @XmlElement(name = "Date", required = true)
  protected Date date;

  /**
   * Ruft den Wert der time-Eigenschaft ab.
   *
   * @return possible object is {@link Time }
   */
  public Time getTime() {
    return time;
  }

  /**
   * Legt den Wert der time-Eigenschaft fest.
   *
   * @param value allowed object is {@link Time }
   */
  public void setTime(Time value) {
    this.time = value;
  }

  /**
   * Ruft den Wert der date-Eigenschaft ab.
   *
   * @return possible object is {@link Date }
   */
  public Date getDate() {
    return date;
  }

  /**
   * Legt den Wert der date-Eigenschaft fest.
   *
   * @param value allowed object is {@link Date }
   */
  public void setDate(Date value) {
    this.date = value;
  }

}
