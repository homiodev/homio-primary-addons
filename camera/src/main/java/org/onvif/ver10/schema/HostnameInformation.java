package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r HostnameInformation complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="HostnameInformation">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="FromDHCP" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="Name" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}HostnameInformationExtension" minOccurs="0"/>
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
        name = "HostnameInformation",
        propOrder = {"fromDHCP", "name", "extension"})
public class HostnameInformation {

    /**
     * -- GETTER --
     *  Ruft den Wert der fromDHCP-Eigenschaft ab.
     */
    @XmlElement(name = "FromDHCP")
    protected boolean fromDHCP;

    /**
     * -- GETTER --
     *  Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String name;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link HostnameInformationExtension }
     */
    @XmlElement(name = "Extension")
    protected HostnameInformationExtension extension;

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
     * Legt den Wert der fromDHCP-Eigenschaft fest.
     */
    public void setFromDHCP(boolean value) {
        this.fromDHCP = value;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link HostnameInformationExtension }
     */
    public void setExtension(HostnameInformationExtension value) {
        this.extension = value;
    }

}
