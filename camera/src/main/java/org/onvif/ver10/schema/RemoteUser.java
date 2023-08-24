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
 * Java-Klasse f�r RemoteUser complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="RemoteUser">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="UseDerivedPassword" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RemoteUser",
        propOrder = {"username", "password", "useDerivedPassword", "any"})
public class RemoteUser {

    /**
     * -- GETTER --
     *  Ruft den Wert der username-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "Username", required = true)
    protected String username;

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
     *  Ruft den Wert der useDerivedPassword-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "UseDerivedPassword")
    protected boolean useDerivedPassword;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

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
     * Legt den Wert der username-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setUsername(String value) {
        this.username = value;
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
     * Legt den Wert der useDerivedPassword-Eigenschaft fest.
     */
    public void setUseDerivedPassword(boolean value) {
        this.useDerivedPassword = value;
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

}
