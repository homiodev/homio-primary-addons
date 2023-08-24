package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r OSDTextConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDTextConfiguration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="DateFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="TimeFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="FontSize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="FontColor" type="{http://www.onvif.org/ver10/schema}OSDColor" minOccurs="0"/>
 *         <element name="BackgroundColor" type="{http://www.onvif.org/ver10/schema}OSDColor" minOccurs="0"/>
 *         <element name="PlainText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}OSDTextConfigurationExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDTextConfiguration",
        propOrder = {
                "type",
                "dateFormat",
                "timeFormat",
                "fontSize",
                "fontColor",
                "backgroundColor",
                "plainText",
                "extension"
        })
public class OSDTextConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Type", required = true)
    protected String type;

    /**
     * -- GETTER --
     *  Ruft den Wert der dateFormat-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "DateFormat")
    protected String dateFormat;

    /**
     * -- GETTER --
     *  Ruft den Wert der timeFormat-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "TimeFormat")
    protected String timeFormat;

    /**
     * -- GETTER --
     *  Ruft den Wert der fontSize-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @XmlElement(name = "FontSize")
    protected Integer fontSize;

    /**
     * -- GETTER --
     *  Ruft den Wert der fontColor-Eigenschaft ab.
     *
     * @return possible object is {@link OSDColor }
     */
    @XmlElement(name = "FontColor")
    protected OSDColor fontColor;

    /**
     * -- GETTER --
     *  Ruft den Wert der backgroundColor-Eigenschaft ab.
     *
     * @return possible object is {@link OSDColor }
     */
    @XmlElement(name = "BackgroundColor")
    protected OSDColor backgroundColor;

    /**
     * -- GETTER --
     *  Ruft den Wert der plainText-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "PlainText")
    protected String plainText;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link OSDTextConfigurationExtension }
     */
    @XmlElement(name = "Extension")
    protected OSDTextConfigurationExtension extension;

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
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Legt den Wert der dateFormat-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDateFormat(String value) {
        this.dateFormat = value;
    }

    /**
     * Legt den Wert der timeFormat-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setTimeFormat(String value) {
        this.timeFormat = value;
    }

    /**
     * Legt den Wert der fontSize-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setFontSize(Integer value) {
        this.fontSize = value;
    }

    /**
     * Legt den Wert der fontColor-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDColor }
     */
    public void setFontColor(OSDColor value) {
        this.fontColor = value;
    }

    /**
     * Legt den Wert der backgroundColor-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDColor }
     */
    public void setBackgroundColor(OSDColor value) {
        this.backgroundColor = value;
    }

    /**
     * Legt den Wert der plainText-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPlainText(String value) {
        this.plainText = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDTextConfigurationExtension }
     */
    public void setExtension(OSDTextConfigurationExtension value) {
        this.extension = value;
    }

}
