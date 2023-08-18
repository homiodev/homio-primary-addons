package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Dot1XConfiguration",
        propOrder = {
                "dot1XConfigurationToken",
                "identity",
                "anonymousID",
                "eapMethod",
                "caCertificateID",
                "eapMethodConfiguration",
                "extension"
        })
public class Dot1XConfiguration {

    @XmlElement(name = "Dot1XConfigurationToken", required = true)
    protected String dot1XConfigurationToken;

    @XmlElement(name = "Identity", required = true)
    protected String identity;

    @XmlElement(name = "AnonymousID")
    protected String anonymousID;

    @XmlElement(name = "EAPMethod")
    protected int eapMethod;

    @XmlElement(name = "CACertificateID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> caCertificateID;

    @XmlElement(name = "EAPMethodConfiguration")
    protected EAPMethodConfiguration eapMethodConfiguration;

    @XmlElement(name = "Extension")
    protected Dot1XConfigurationExtension extension;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der dot1XConfigurationToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getDot1XConfigurationToken() {
        return dot1XConfigurationToken;
    }

    /**
     * Legt den Wert der dot1XConfigurationToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDot1XConfigurationToken(String value) {
        this.dot1XConfigurationToken = value;
    }

    /**
     * Ruft den Wert der identity-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Legt den Wert der identity-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setIdentity(String value) {
        this.identity = value;
    }

    /**
     * Ruft den Wert der anonymousID-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getAnonymousID() {
        return anonymousID;
    }

    /**
     * Legt den Wert der anonymousID-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setAnonymousID(String value) {
        this.anonymousID = value;
    }

    /**
     * Ruft den Wert der eapMethod-Eigenschaft ab.
     */
    public int getEAPMethod() {
        return eapMethod;
    }

    /**
     * Legt den Wert der eapMethod-Eigenschaft fest.
     */
    public void setEAPMethod(int value) {
        this.eapMethod = value;
    }

    /**
     * Gets the value of the caCertificateID property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * caCertificateID property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getCACertificateID().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getCACertificateID() {
        if (caCertificateID == null) {
            caCertificateID = new ArrayList<String>();
        }
        return this.caCertificateID;
    }

    /**
     * Ruft den Wert der eapMethodConfiguration-Eigenschaft ab.
     *
     * @return possible object is {@link EAPMethodConfiguration }
     */
    public EAPMethodConfiguration getEAPMethodConfiguration() {
        return eapMethodConfiguration;
    }

    /**
     * Legt den Wert der eapMethodConfiguration-Eigenschaft fest.
     *
     * @param value allowed object is {@link EAPMethodConfiguration }
     */
    public void setEAPMethodConfiguration(EAPMethodConfiguration value) {
        this.eapMethodConfiguration = value;
    }

    /**
     * Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link Dot1XConfigurationExtension }
     */
    public Dot1XConfigurationExtension getExtension() {
        return extension;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot1XConfigurationExtension }
     */
    public void setExtension(Dot1XConfigurationExtension value) {
        this.extension = value;
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
