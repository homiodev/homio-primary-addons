package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Date",
        propOrder = {"year", "month", "day"})
public class Date {

    /**
     * -- GETTER --
     *  Ruft den Wert der year-Eigenschaft ab.
     */
    @XmlElement(name = "Year")
    protected int year;

    /**
     * -- GETTER --
     *  Ruft den Wert der month-Eigenschaft ab.
     */
    @XmlElement(name = "Month")
    protected int month;

    /**
     * -- GETTER --
     *  Ruft den Wert der day-Eigenschaft ab.
     */
    @XmlElement(name = "Day")
    protected int day;

    /**
     * Legt den Wert der year-Eigenschaft fest.
     */
    public void setYear(int value) {
        this.year = value;
    }

    /**
     * Legt den Wert der month-Eigenschaft fest.
     */
    public void setMonth(int value) {
        this.month = value;
    }

    /**
     * Legt den Wert der day-Eigenschaft fest.
     */
    public void setDay(int value) {
        this.day = value;
    }
}
