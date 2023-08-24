package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r OSDTextOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDTextOptions">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Type" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         <element name="FontSizeRange" type="{http://www.onvif.org/ver10/schema}IntRange" minOccurs="0"/>
 *         <element name="DateFormat" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="TimeFormat" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="FontColor" type="{http://www.onvif.org/ver10/schema}OSDColorOptions" minOccurs="0"/>
 *         <element name="BackgroundColor" type="{http://www.onvif.org/ver10/schema}OSDColorOptions" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}OSDTextOptionsExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDTextOptions",
        propOrder = {
                "type",
                "fontSizeRange",
                "dateFormat",
                "timeFormat",
                "fontColor",
                "backgroundColor",
                "extension"
        })
public class OSDTextOptions {

    @XmlElement(name = "Type", required = true)
    protected List<String> type;

    /**
     * -- GETTER --
     *  Ruft den Wert der fontSizeRange-Eigenschaft ab.
     *
     * @return possible object is {@link IntRange }
     */
    @Getter @XmlElement(name = "FontSizeRange")
    protected IntRange fontSizeRange;

    @XmlElement(name = "DateFormat")
    protected List<String> dateFormat;

    @XmlElement(name = "TimeFormat")
    protected List<String> timeFormat;

    /**
     * -- GETTER --
     *  Ruft den Wert der fontColor-Eigenschaft ab.
     *
     * @return possible object is {@link OSDColorOptions }
     */
    @Getter @XmlElement(name = "FontColor")
    protected OSDColorOptions fontColor;

    /**
     * -- GETTER --
     *  Ruft den Wert der backgroundColor-Eigenschaft ab.
     *
     * @return possible object is {@link OSDColorOptions }
     */
    @Getter @XmlElement(name = "BackgroundColor")
    protected OSDColorOptions backgroundColor;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link OSDTextOptionsExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected OSDTextOptionsExtension extension;

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
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the type property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the type
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getType().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getType() {
        if (type == null) {
            type = new ArrayList<String>();
        }
        return this.type;
    }

    /**
     * Legt den Wert der fontSizeRange-Eigenschaft fest.
     *
     * @param value allowed object is {@link IntRange }
     */
    public void setFontSizeRange(IntRange value) {
        this.fontSizeRange = value;
    }

    /**
     * Gets the value of the dateFormat property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the dateFormat
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getDateFormat().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new ArrayList<String>();
        }
        return this.dateFormat;
    }

    /**
     * Gets the value of the timeFormat property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the timeFormat
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getTimeFormat().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getTimeFormat() {
        if (timeFormat == null) {
            timeFormat = new ArrayList<String>();
        }
        return this.timeFormat;
    }

    /**
     * Legt den Wert der fontColor-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDColorOptions }
     */
    public void setFontColor(OSDColorOptions value) {
        this.fontColor = value;
    }

    /**
     * Legt den Wert der backgroundColor-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDColorOptions }
     */
    public void setBackgroundColor(OSDColorOptions value) {
        this.backgroundColor = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDTextOptionsExtension }
     */
    public void setExtension(OSDTextOptionsExtension value) {
        this.extension = value;
    }

}
