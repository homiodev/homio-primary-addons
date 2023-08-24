package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r FocusConfiguration20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="FocusConfiguration20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="AutoFocusMode" type="{http://www.onvif.org/ver10/schema}AutoFocusMode"/>
 *         <element name="DefaultSpeed" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="NearLimit" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="FarLimit" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}FocusConfiguration20Extension" minOccurs="0"/>
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
        name = "FocusConfiguration20",
        propOrder = {"autoFocusMode", "defaultSpeed", "nearLimit", "farLimit", "extension"})
public class FocusConfiguration20 {

    /**
     * -- GETTER --
     *  Ruft den Wert der autoFocusMode-Eigenschaft ab.
     *
     * @return possible object is {@link AutoFocusMode }
     */
    @XmlElement(name = "AutoFocusMode", required = true)
    protected AutoFocusMode autoFocusMode;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultSpeed-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "DefaultSpeed")
    protected Float defaultSpeed;

    /**
     * -- GETTER --
     *  Ruft den Wert der nearLimit-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "NearLimit")
    protected Float nearLimit;

    /**
     * -- GETTER --
     *  Ruft den Wert der farLimit-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "FarLimit")
    protected Float farLimit;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link FocusConfiguration20Extension }
     */
    @XmlElement(name = "Extension")
    protected FocusConfiguration20Extension extension;

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
     * Legt den Wert der autoFocusMode-Eigenschaft fest.
     *
     * @param value allowed object is {@link AutoFocusMode }
     */
    public void setAutoFocusMode(AutoFocusMode value) {
        this.autoFocusMode = value;
    }

    /**
     * Legt den Wert der defaultSpeed-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setDefaultSpeed(Float value) {
        this.defaultSpeed = value;
    }

    /**
     * Legt den Wert der nearLimit-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setNearLimit(Float value) {
        this.nearLimit = value;
    }

    /**
     * Legt den Wert der farLimit-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setFarLimit(Float value) {
        this.farLimit = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link FocusConfiguration20Extension }
     */
    public void setExtension(FocusConfiguration20Extension value) {
        this.extension = value;
    }

}
