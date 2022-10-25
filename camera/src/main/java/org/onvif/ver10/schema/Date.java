package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Date", propOrder = {"year", "month", "day"})
public class Date {

  @XmlElement(name = "Year")
  protected int year;
  @XmlElement(name = "Month")
  protected int month;
  @XmlElement(name = "Day")
  protected int day;

  /**
   * Ruft den Wert der year-Eigenschaft ab.
   */
  public int getYear() {
    return year;
  }

  /**
   * Legt den Wert der year-Eigenschaft fest.
   */
  public void setYear(int value) {
    this.year = value;
  }

  /**
   * Ruft den Wert der month-Eigenschaft ab.
   */
  public int getMonth() {
    return month;
  }

  /**
   * Legt den Wert der month-Eigenschaft fest.
   */
  public void setMonth(int value) {
    this.month = value;
  }

  /**
   * Ruft den Wert der day-Eigenschaft ab.
   */
  public int getDay() {
    return day;
  }

  /**
   * Legt den Wert der day-Eigenschaft fest.
   */
  public void setDay(int value) {
    this.day = value;
  }

}
