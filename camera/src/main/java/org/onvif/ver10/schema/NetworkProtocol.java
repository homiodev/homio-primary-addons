package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r NetworkProtocol complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="NetworkProtocol">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Name" type="{http://www.onvif.org/ver10/schema}NetworkProtocolType"/>
 *         <element name="Enabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="Port" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}NetworkProtocolExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "NetworkProtocol",
        propOrder = {"name", "enabled", "port", "extension"})
public class NetworkProtocol {

    /**
     * -- GETTER --
     *  Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is {@link NetworkProtocolType }
     */
    @Getter @XmlElement(name = "Name", required = true)
    protected NetworkProtocolType name;

    /**
     * -- GETTER --
     *  Ruft den Wert der enabled-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "Enabled")
    protected boolean enabled;

    @XmlElement(name = "Port", type = Integer.class)
    protected List<Integer> port;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link NetworkProtocolExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected NetworkProtocolExtension extension;

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
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is {@link NetworkProtocolType }
     */
    public void setName(NetworkProtocolType value) {
        this.name = value;
    }

    /**
     * Legt den Wert der enabled-Eigenschaft fest.
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the port property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the port
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getPort().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Integer }
     */
    public List<Integer> getPort() {
        if (port == null) {
            port = new ArrayList<Integer>();
        }
        return this.port;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link NetworkProtocolExtension }
     */
    public void setExtension(NetworkProtocolExtension value) {
        this.extension = value;
    }

}
