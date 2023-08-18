package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

/**
 * Java-Klasse f�r FocusOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="FocusOptions">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="AutoFocusModes" type="{http://www.onvif.org/ver10/schema}AutoFocusMode" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="DefaultSpeed" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *         <element name="NearLimit" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *         <element name="FarLimit" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FocusOptions",
        propOrder = {"autoFocusModes", "defaultSpeed", "nearLimit", "farLimit"})
public class FocusOptions {

    @XmlElement(name = "AutoFocusModes")
    protected List<AutoFocusMode> autoFocusModes;

    @XmlElement(name = "DefaultSpeed", required = true)
    protected FloatRange defaultSpeed;

    @XmlElement(name = "NearLimit", required = true)
    protected FloatRange nearLimit;

    @XmlElement(name = "FarLimit", required = true)
    protected FloatRange farLimit;

    /**
     * Gets the value of the autoFocusModes property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * autoFocusModes property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getAutoFocusModes().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link AutoFocusMode }
     */
    public List<AutoFocusMode> getAutoFocusModes() {
        if (autoFocusModes == null) {
            autoFocusModes = new ArrayList<AutoFocusMode>();
        }
        return this.autoFocusModes;
    }

    /**
     * Ruft den Wert der defaultSpeed-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    public FloatRange getDefaultSpeed() {
        return defaultSpeed;
    }

    /**
     * Legt den Wert der defaultSpeed-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setDefaultSpeed(FloatRange value) {
        this.defaultSpeed = value;
    }

    /**
     * Ruft den Wert der nearLimit-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    public FloatRange getNearLimit() {
        return nearLimit;
    }

    /**
     * Legt den Wert der nearLimit-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setNearLimit(FloatRange value) {
        this.nearLimit = value;
    }

    /**
     * Ruft den Wert der farLimit-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    public FloatRange getFarLimit() {
        return farLimit;
    }

    /**
     * Legt den Wert der farLimit-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setFarLimit(FloatRange value) {
        this.farLimit = value;
    }
}
