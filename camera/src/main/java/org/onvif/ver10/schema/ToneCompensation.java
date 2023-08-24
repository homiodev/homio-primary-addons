package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r ToneCompensation complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * &lt;complexType name="ToneCompensation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Mode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Level" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}ToneCompensationExtension" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ToneCompensation",
        propOrder = {"mode", "level", "extension"})
public class ToneCompensation {

    /**
     * -- GETTER --
     *  Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Mode", required = true)
    protected String mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der level-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Level")
    protected Float level;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ToneCompensationExtension }
     */
    @XmlElement(name = "Extension")
    protected ToneCompensationExtension extension;

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
     * Legt den Wert der mode-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setMode(String value) {
        this.mode = value;
    }

    /**
     * Legt den Wert der level-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setLevel(Float value) {
        this.level = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ToneCompensationExtension }
     */
    public void setExtension(ToneCompensationExtension value) {
        this.extension = value;
    }

}
