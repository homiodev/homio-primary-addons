package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r Dot11SecurityConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Dot11SecurityConfiguration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}Dot11SecurityMode"/>
 *         <element name="Algorithm" type="{http://www.onvif.org/ver10/schema}Dot11Cipher" minOccurs="0"/>
 *         <element name="PSK" type="{http://www.onvif.org/ver10/schema}Dot11PSKSet" minOccurs="0"/>
 *         <element name="Dot1X" type="{http://www.onvif.org/ver10/schema}ReferenceToken" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}Dot11SecurityConfigurationExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Dot11SecurityConfiguration",
        propOrder = {"mode", "algorithm", "psk", "dot1X", "extension"})
public class Dot11SecurityConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11SecurityMode }
     */
    @Getter @XmlElement(name = "Mode", required = true)
    protected Dot11SecurityMode mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der algorithm-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11Cipher }
     */
    @Getter @XmlElement(name = "Algorithm")
    protected Dot11Cipher algorithm;

    @XmlElement(name = "PSK")
    protected Dot11PSKSet psk;

    /**
     * -- GETTER --
     *  Ruft den Wert der dot1X-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "Dot1X")
    protected String dot1X;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11SecurityConfigurationExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected Dot11SecurityConfigurationExtension extension;

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
     * Legt den Wert der mode-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11SecurityMode }
     */
    public void setMode(Dot11SecurityMode value) {
        this.mode = value;
    }

    /**
     * Legt den Wert der algorithm-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11Cipher }
     */
    public void setAlgorithm(Dot11Cipher value) {
        this.algorithm = value;
    }

    /**
     * Ruft den Wert der psk-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11PSKSet }
     */
    public Dot11PSKSet getPSK() {
        return psk;
    }

    /**
     * Legt den Wert der psk-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11PSKSet }
     */
    public void setPSK(Dot11PSKSet value) {
        this.psk = value;
    }

    /**
     * Legt den Wert der dot1X-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDot1X(String value) {
        this.dot1X = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11SecurityConfigurationExtension }
     */
    public void setExtension(Dot11SecurityConfigurationExtension value) {
        this.extension = value;
    }

}
