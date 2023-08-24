package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r Dot11PSKSet complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Dot11PSKSet">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Key" type="{http://www.onvif.org/ver10/schema}Dot11PSK" minOccurs="0"/>
 *         <element name="Passphrase" type="{http://www.onvif.org/ver10/schema}Dot11PSKPassphrase" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}Dot11PSKSetExtension" minOccurs="0"/>
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
        name = "Dot11PSKSet",
        propOrder = {"key", "passphrase", "extension"})
public class Dot11PSKSet {

    /**
     * -- GETTER --
     *  Ruft den Wert der key-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Key", type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] key;

    /**
     * -- GETTER --
     *  Ruft den Wert der passphrase-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Passphrase")
    protected String passphrase;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11PSKSetExtension }
     */
    @XmlElement(name = "Extension")
    protected Dot11PSKSetExtension extension;

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
     * Legt den Wert der key-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setKey(byte[] value) {
        this.key = value;
    }

    /**
     * Legt den Wert der passphrase-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPassphrase(String value) {
        this.passphrase = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11PSKSetExtension }
     */
    public void setExtension(Dot11PSKSetExtension value) {
        this.extension = value;
    }

}
