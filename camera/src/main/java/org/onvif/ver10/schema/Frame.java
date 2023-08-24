package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r Frame complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Frame">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PTZStatus" type="{http://www.onvif.org/ver10/schema}PTZStatus" minOccurs="0"/>
 *         <element name="Transformation" type="{http://www.onvif.org/ver10/schema}Transformation" minOccurs="0"/>
 *         <element name="Object" type="{http://www.onvif.org/ver10/schema}Object" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="ObjectTree" type="{http://www.onvif.org/ver10/schema}ObjectTree" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}FrameExtension" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="UtcTime" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Frame",
        propOrder = {"ptzStatus", "transformation", "object", "objectTree", "extension"})
public class Frame {

    @XmlElement(name = "PTZStatus")
    protected PTZStatus ptzStatus;

    /**
     * -- GETTER --
     *  Ruft den Wert der transformation-Eigenschaft ab.
     *
     * @return possible object is {@link Transformation }
     */
    @Getter @XmlElement(name = "Transformation")
    protected Transformation transformation;

    @XmlElement(name = "Object")
    protected List<Object> object;

    /**
     * -- GETTER --
     *  Ruft den Wert der objectTree-Eigenschaft ab.
     *
     * @return possible object is {@link ObjectTree }
     */
    @Getter @XmlElement(name = "ObjectTree")
    protected ObjectTree objectTree;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link FrameExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected FrameExtension extension;

    /**
     * -- GETTER --
     *  Ruft den Wert der utcTime-Eigenschaft ab.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     */
    @Getter @XmlAttribute(name = "UtcTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar utcTime;

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
     * Ruft den Wert der ptzStatus-Eigenschaft ab.
     *
     * @return possible object is {@link PTZStatus }
     */
    public PTZStatus getPTZStatus() {
        return ptzStatus;
    }

    /**
     * Legt den Wert der ptzStatus-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZStatus }
     */
    public void setPTZStatus(PTZStatus value) {
        this.ptzStatus = value;
    }

    /**
     * Legt den Wert der transformation-Eigenschaft fest.
     *
     * @param value allowed object is {@link Transformation }
     */
    public void setTransformation(Transformation value) {
        this.transformation = value;
    }

    /**
     * Gets the value of the object property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the object
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getObject().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Object }
     */
    public List<Object> getObject() {
        if (object == null) {
            object = new ArrayList<Object>();
        }
        return this.object;
    }

    /**
     * Legt den Wert der objectTree-Eigenschaft fest.
     *
     * @param value allowed object is {@link ObjectTree }
     */
    public void setObjectTree(ObjectTree value) {
        this.objectTree = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link FrameExtension }
     */
    public void setExtension(FrameExtension value) {
        this.extension = value;
    }

    /**
     * Legt den Wert der utcTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setUtcTime(XMLGregorianCalendar value) {
        this.utcTime = value;
    }

}
