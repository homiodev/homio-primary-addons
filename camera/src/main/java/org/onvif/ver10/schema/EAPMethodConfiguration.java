package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r EAPMethodConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="EAPMethodConfiguration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="TLSConfiguration" type="{http://www.onvif.org/ver10/schema}TLSConfiguration" minOccurs="0"/>
 *         <element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}EapMethodExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "EAPMethodConfiguration",
        propOrder = {"tlsConfiguration", "password", "extension"})
public class EAPMethodConfiguration {

    @XmlElement(name = "TLSConfiguration")
    protected TLSConfiguration tlsConfiguration;

    /**
     * -- GETTER --
     *  Ruft den Wert der password-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "Password")
    protected String password;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link EapMethodExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected EapMethodExtension extension;

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
     * Ruft den Wert der tlsConfiguration-Eigenschaft ab.
     *
     * @return possible object is {@link TLSConfiguration }
     */
    public TLSConfiguration getTLSConfiguration() {
        return tlsConfiguration;
    }

    /**
     * Legt den Wert der tlsConfiguration-Eigenschaft fest.
     *
     * @param value allowed object is {@link TLSConfiguration }
     */
    public void setTLSConfiguration(TLSConfiguration value) {
        this.tlsConfiguration = value;
    }

    /**
     * Legt den Wert der password-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link EapMethodExtension }
     */
    public void setExtension(EapMethodExtension value) {
        this.extension = value;
    }

}
