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
 * Java-Klasse f�r MetadataAttributes complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="MetadataAttributes">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="CanContainPTZ" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="CanContainAnalytics" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="CanContainNotifications" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="PtzSpaces" type="{http://www.onvif.org/ver10/schema}StringAttrList" />
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MetadataAttributes",
        propOrder = {"canContainPTZ", "canContainAnalytics", "canContainNotifications", "any"})
public class MetadataAttributes {

    /**
     * -- GETTER --
     *  Ruft den Wert der canContainPTZ-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "CanContainPTZ")
    protected boolean canContainPTZ;

    /**
     * -- GETTER --
     *  Ruft den Wert der canContainAnalytics-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "CanContainAnalytics")
    protected boolean canContainAnalytics;

    /**
     * -- GETTER --
     *  Ruft den Wert der canContainNotifications-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "CanContainNotifications")
    protected boolean canContainNotifications;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlAttribute(name = "PtzSpaces")
    protected List<String> ptzSpaces;

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
     * Legt den Wert der canContainPTZ-Eigenschaft fest.
     */
    public void setCanContainPTZ(boolean value) {
        this.canContainPTZ = value;
    }

    /**
     * Legt den Wert der canContainAnalytics-Eigenschaft fest.
     */
    public void setCanContainAnalytics(boolean value) {
        this.canContainAnalytics = value;
    }

    /**
     * Legt den Wert der canContainNotifications-Eigenschaft fest.
     */
    public void setCanContainNotifications(boolean value) {
        this.canContainNotifications = value;
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
     * Gets the value of the ptzSpaces property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the ptzSpaces
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getPtzSpaces().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getPtzSpaces() {
        if (ptzSpaces == null) {
            ptzSpaces = new ArrayList<String>();
        }
        return this.ptzSpaces;
    }

}
