package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java-Klasse f�r SearchCapabilities complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="SearchCapabilities">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="XAddr" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         <element name="MetadataSearch" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        name = "SearchCapabilities",
        propOrder = {"xAddr", "metadataSearch", "any"})
public class SearchCapabilities {

    @XmlElement(name = "XAddr", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String xAddr;

    @XmlElement(name = "MetadataSearch")
    protected boolean metadataSearch;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der xAddr-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getXAddr() {
        return xAddr;
    }

    /**
     * Legt den Wert der xAddr-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setXAddr(String value) {
        this.xAddr = value;
    }

    /**
     * Ruft den Wert der metadataSearch-Eigenschaft ab.
     */
    public boolean isMetadataSearch() {
        return metadataSearch;
    }

    /**
     * Legt den Wert der metadataSearch-Eigenschaft fest.
     */
    public void setMetadataSearch(boolean value) {
        this.metadataSearch = value;
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
