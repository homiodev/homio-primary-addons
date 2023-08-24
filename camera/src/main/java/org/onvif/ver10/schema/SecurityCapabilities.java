package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java-Klasse f�r SecurityCapabilities complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="SecurityCapabilities">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="TLS1.1" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="TLS1.2" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="OnboardKeyGeneration" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="AccessPolicyConfig" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="X.509Token" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="SAMLToken" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="KerberosToken" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="RELToken" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}SecurityCapabilitiesExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "SecurityCapabilities",
        propOrder = {
                "tls11",
                "tls12",
                "onboardKeyGeneration",
                "accessPolicyConfig",
                "x509Token",
                "samlToken",
                "kerberosToken",
                "relToken",
                "any",
                "extension"
        })
public class SecurityCapabilities {

    @XmlElement(name = "TLS1.1")
    protected boolean tls11;

    @XmlElement(name = "TLS1.2")
    protected boolean tls12;

    /**
     * -- GETTER --
     *  Ruft den Wert der onboardKeyGeneration-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "OnboardKeyGeneration")
    protected boolean onboardKeyGeneration;

    /**
     * -- GETTER --
     *  Ruft den Wert der accessPolicyConfig-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "AccessPolicyConfig")
    protected boolean accessPolicyConfig;

    /**
     * -- GETTER --
     *  Ruft den Wert der x509Token-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "X.509Token")
    protected boolean x509Token;

    @XmlElement(name = "SAMLToken")
    protected boolean samlToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der kerberosToken-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "KerberosToken")
    protected boolean kerberosToken;

    @XmlElement(name = "RELToken")
    protected boolean relToken;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link SecurityCapabilitiesExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected SecurityCapabilitiesExtension extension;

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
     * Ruft den Wert der tls11-Eigenschaft ab.
     */
    public boolean isTLS11() {
        return tls11;
    }

    /**
     * Legt den Wert der tls11-Eigenschaft fest.
     */
    public void setTLS11(boolean value) {
        this.tls11 = value;
    }

    /**
     * Ruft den Wert der tls12-Eigenschaft ab.
     */
    public boolean isTLS12() {
        return tls12;
    }

    /**
     * Legt den Wert der tls12-Eigenschaft fest.
     */
    public void setTLS12(boolean value) {
        this.tls12 = value;
    }

    /**
     * Legt den Wert der onboardKeyGeneration-Eigenschaft fest.
     */
    public void setOnboardKeyGeneration(boolean value) {
        this.onboardKeyGeneration = value;
    }

    /**
     * Legt den Wert der accessPolicyConfig-Eigenschaft fest.
     */
    public void setAccessPolicyConfig(boolean value) {
        this.accessPolicyConfig = value;
    }

    /**
     * Legt den Wert der x509Token-Eigenschaft fest.
     */
    public void setX509Token(boolean value) {
        this.x509Token = value;
    }

    /**
     * Ruft den Wert der samlToken-Eigenschaft ab.
     */
    public boolean isSAMLToken() {
        return samlToken;
    }

    /**
     * Legt den Wert der samlToken-Eigenschaft fest.
     */
    public void setSAMLToken(boolean value) {
        this.samlToken = value;
    }

    /**
     * Legt den Wert der kerberosToken-Eigenschaft fest.
     */
    public void setKerberosToken(boolean value) {
        this.kerberosToken = value;
    }

    /**
     * Ruft den Wert der relToken-Eigenschaft ab.
     */
    public boolean isRELToken() {
        return relToken;
    }

    /**
     * Legt den Wert der relToken-Eigenschaft fest.
     */
    public void setRELToken(boolean value) {
        this.relToken = value;
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
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link SecurityCapabilitiesExtension }
     */
    public void setExtension(SecurityCapabilitiesExtension value) {
        this.extension = value;
    }

}
