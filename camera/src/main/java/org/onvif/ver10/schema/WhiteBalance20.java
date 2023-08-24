package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r WhiteBalance20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="WhiteBalance20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}WhiteBalanceMode"/>
 *         <element name="CrGain" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="CbGain" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}WhiteBalance20Extension" minOccurs="0"/>
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
        name = "WhiteBalance20",
        propOrder = {"mode", "crGain", "cbGain", "extension"})
public class WhiteBalance20 {

    /**
     * -- GETTER --
     *  Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link WhiteBalanceMode }
     */
    @XmlElement(name = "Mode", required = true)
    protected WhiteBalanceMode mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der crGain-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "CrGain")
    protected Float crGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der cbGain-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "CbGain")
    protected Float cbGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link WhiteBalance20Extension }
     */
    @XmlElement(name = "Extension")
    protected WhiteBalance20Extension extension;

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
     * @param value allowed object is {@link WhiteBalanceMode }
     */
    public void setMode(WhiteBalanceMode value) {
        this.mode = value;
    }

    /**
     * Legt den Wert der crGain-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setCrGain(Float value) {
        this.crGain = value;
    }

    /**
     * Legt den Wert der cbGain-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setCbGain(Float value) {
        this.cbGain = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link WhiteBalance20Extension }
     */
    public void setExtension(WhiteBalance20Extension value) {
        this.extension = value;
    }

}
