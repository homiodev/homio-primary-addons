package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ConfigDescription",
        propOrder = {"parameters", "messages", "extension"})
public class ConfigDescription {

    /**
     * -- GETTER --
     *  Ruft den Wert der parameters-Eigenschaft ab.
     *
     * @return possible object is {@link ItemListDescription }
     */
    @Getter @XmlElement(name = "Parameters", required = true)
    protected ItemListDescription parameters;

    @XmlElement(name = "Messages")
    protected List<ConfigDescription.Messages> messages;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ConfigDescriptionExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected ConfigDescriptionExtension extension;

    /**
     * -- GETTER --
     *  Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is {@link QName }
     */
    @Getter @XmlAttribute(name = "Name", required = true)
    protected QName name;

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
     * Legt den Wert der parameters-Eigenschaft fest.
     *
     * @param value allowed object is {@link ItemListDescription }
     */
    public void setParameters(ItemListDescription value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the messages property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the messages
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getMessages().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link ConfigDescription.Messages }
     */
    public List<ConfigDescription.Messages> getMessages() {
        if (messages == null) {
            messages = new ArrayList<ConfigDescription.Messages>();
        }
        return this.messages;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ConfigDescriptionExtension }
     */
    public void setExtension(ConfigDescriptionExtension value) {
        this.extension = value;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is {@link QName }
     */
    public void setName(QName value) {
        this.name = value;
    }

    /**
     * Java-Klasse f�r anonymous complex type.
     *
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
     * ist.
     *
     * <pre>
     * <complexType>
     *   <complexContent>
     *     <extension base="{http://www.onvif.org/ver10/schema}MessageDescription">
     *       <sequence>
     *         <element name="ParentTopic" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       </sequence>
     *       <anyAttribute processContents='lax'/>
     *     </extension>
     *   </complexContent>
     * </complexType>
     * </pre>
     */
    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"parentTopic"})
    public static class Messages extends MessageDescription {

        /**
         * -- GETTER --
         *  Ruft den Wert der parentTopic-Eigenschaft ab.
         *
         * @return possible object is {@link String }
         */
        @XmlElement(name = "ParentTopic", required = true)
        protected String parentTopic;

        /**
         * Legt den Wert der parentTopic-Eigenschaft fest.
         *
         * @param value allowed object is {@link String }
         */
        public void setParentTopic(String value) {
            this.parentTopic = value;
        }
    }
}
