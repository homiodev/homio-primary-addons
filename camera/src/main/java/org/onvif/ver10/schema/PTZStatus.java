package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

/**
 * Java-Klasse f�r PTZStatus complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PTZStatus">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Position" type="{http://www.onvif.org/ver10/schema}PTZVector" minOccurs="0"/>
 *         <element name="MoveStatus" type="{http://www.onvif.org/ver10/schema}PTZMoveStatus" minOccurs="0"/>
 *         <element name="Error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="UtcTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "PTZStatus",
    propOrder = {"position", "moveStatus", "error", "utcTime", "any"})
public class PTZStatus {

    @XmlElement(name = "Position")
    protected PTZVector position;

    @XmlElement(name = "MoveStatus")
    protected PTZMoveStatus moveStatus;

    @XmlElement(name = "Error")
    protected String error;

    @XmlElement(name = "UtcTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar utcTime;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlAnyAttribute private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der position-Eigenschaft ab.
     *
     * @return possible object is {@link PTZVector }
     */
    public PTZVector getPosition() {
        return position;
    }

    /**
     * Legt den Wert der position-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZVector }
     */
    public void setPosition(PTZVector value) {
        this.position = value;
    }

    /**
     * Ruft den Wert der moveStatus-Eigenschaft ab.
     *
     * @return possible object is {@link PTZMoveStatus }
     */
    public PTZMoveStatus getMoveStatus() {
        return moveStatus;
    }

    /**
     * Legt den Wert der moveStatus-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZMoveStatus }
     */
    public void setMoveStatus(PTZMoveStatus value) {
        this.moveStatus = value;
    }

    /**
     * Ruft den Wert der error-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getError() {
        return error;
    }

    /**
     * Legt den Wert der error-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setError(String value) {
        this.error = value;
    }

    /**
     * Ruft den Wert der utcTime-Eigenschaft ab.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getUtcTime() {
        return utcTime;
    }

    /**
     * Legt den Wert der utcTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setUtcTime(XMLGregorianCalendar value) {
        this.utcTime = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the any
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Element } {@link
     * java.lang.Object }
     */
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>the map is keyed by the name of the attribute and the value is the string value of the
     * attribute.
     *
     * <p>the map returned by this method is live, and you can add new attribute by updating the map
     * directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}