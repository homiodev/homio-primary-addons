package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r MessageDescription complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="MessageDescription">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Source" type="{http://www.onvif.org/ver10/schema}ItemListDescription" minOccurs="0"/>
 *         <element name="Key" type="{http://www.onvif.org/ver10/schema}ItemListDescription" minOccurs="0"/>
 *         <element name="Data" type="{http://www.onvif.org/ver10/schema}ItemListDescription" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}MessageDescriptionExtension" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="IsProperty" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MessageDescription",
        propOrder = {"source", "key", "data", "extension"})
@XmlSeeAlso({org.onvif.ver10.schema.ConfigDescription.Messages.class})
public class MessageDescription {

    /**
     * -- GETTER --
     *  Ruft den Wert der source-Eigenschaft ab.
     *
     * @return possible object is {@link ItemListDescription }
     */
    @Getter @XmlElement(name = "Source")
    protected ItemListDescription source;

    /**
     * -- GETTER --
     *  Ruft den Wert der key-Eigenschaft ab.
     *
     * @return possible object is {@link ItemListDescription }
     */
    @Getter @XmlElement(name = "Key")
    protected ItemListDescription key;

    /**
     * -- GETTER --
     *  Ruft den Wert der data-Eigenschaft ab.
     *
     * @return possible object is {@link ItemListDescription }
     */
    @Getter @XmlElement(name = "Data")
    protected ItemListDescription data;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link MessageDescriptionExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected MessageDescriptionExtension extension;

    @XmlAttribute(name = "IsProperty")
    protected Boolean isProperty;

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
     * Legt den Wert der source-Eigenschaft fest.
     *
     * @param value allowed object is {@link ItemListDescription }
     */
    public void setSource(ItemListDescription value) {
        this.source = value;
    }

    /**
     * Legt den Wert der key-Eigenschaft fest.
     *
     * @param value allowed object is {@link ItemListDescription }
     */
    public void setKey(ItemListDescription value) {
        this.key = value;
    }

    /**
     * Legt den Wert der data-Eigenschaft fest.
     *
     * @param value allowed object is {@link ItemListDescription }
     */
    public void setData(ItemListDescription value) {
        this.data = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link MessageDescriptionExtension }
     */
    public void setExtension(MessageDescriptionExtension value) {
        this.extension = value;
    }

    /**
     * Ruft den Wert der isProperty-Eigenschaft ab.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isIsProperty() {
        return isProperty;
    }

    /**
     * Legt den Wert der isProperty-Eigenschaft fest.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setIsProperty(Boolean value) {
        this.isProperty = value;
    }

}
