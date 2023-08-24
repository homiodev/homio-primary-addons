package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r MaximumNumberOfOSDs complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="MaximumNumberOfOSDs">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="Total" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="Image" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="PlainText" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="Date" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="DateAndTime" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaximumNumberOfOSDs")
public class MaximumNumberOfOSDs {

    /**
     * -- GETTER --
     *  Ruft den Wert der total-Eigenschaft ab.
     */
    @XmlAttribute(name = "Total", required = true)
    protected int total;

    /**
     * -- GETTER --
     *  Ruft den Wert der image-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @XmlAttribute(name = "Image")
    protected Integer image;

    /**
     * -- GETTER --
     *  Ruft den Wert der plainText-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @XmlAttribute(name = "PlainText")
    protected Integer plainText;

    /**
     * -- GETTER --
     *  Ruft den Wert der date-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @XmlAttribute(name = "Date")
    protected Integer date;

    /**
     * -- GETTER --
     *  Ruft den Wert der time-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @XmlAttribute(name = "Time")
    protected Integer time;

    /**
     * -- GETTER --
     *  Ruft den Wert der dateAndTime-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @XmlAttribute(name = "DateAndTime")
    protected Integer dateAndTime;

    /**
     * -- GETTER --
     *  Gets a map that contains attributes that aren't bound to any typed property on this class.
     *  <p>the map is keyed by the name of the attribute and the value is the string value of the
     *  attribute.
     *  <p>the map returned by this method is live, and you can add new attribute by updating the map
     *  directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Legt den Wert der total-Eigenschaft fest.
     */
    public void setTotal(int value) {
        this.total = value;
    }

    /**
     * Legt den Wert der image-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setImage(Integer value) {
        this.image = value;
    }

    /**
     * Legt den Wert der plainText-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setPlainText(Integer value) {
        this.plainText = value;
    }

    /**
     * Legt den Wert der date-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setDate(Integer value) {
        this.date = value;
    }

    /**
     * Legt den Wert der time-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setTime(Integer value) {
        this.time = value;
    }

    /**
     * Legt den Wert der dateAndTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setDateAndTime(Integer value) {
        this.dateAndTime = value;
    }

}
