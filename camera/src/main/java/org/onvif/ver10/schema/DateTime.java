package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "DateTime",
        propOrder = {"time", "date"})
public class DateTime {

    /**
     * -- GETTER --
     *  Ruft den Wert der time-Eigenschaft ab.
     *
     * @return possible object is {@link Time }
     */
    @XmlElement(name = "Time", required = true)
    protected Time time;

    /**
     * -- GETTER --
     *  Ruft den Wert der date-Eigenschaft ab.
     *
     * @return possible object is {@link Date }
     */
    @XmlElement(name = "Date", required = true)
    protected Date date;

    /**
     * Legt den Wert der time-Eigenschaft fest.
     *
     * @param value allowed object is {@link Time }
     */
    public void setTime(Time value) {
        this.time = value;
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
