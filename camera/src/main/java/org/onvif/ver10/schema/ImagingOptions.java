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
 * Java-Klasse f�r ImagingOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="ImagingOptions">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="BacklightCompensation" type="{http://www.onvif.org/ver10/schema}BacklightCompensationOptions"/>
 *         <element name="Brightness" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *         <element name="ColorSaturation" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *         <element name="Contrast" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *         <element name="Exposure" type="{http://www.onvif.org/ver10/schema}ExposureOptions"/>
 *         <element name="Focus" type="{http://www.onvif.org/ver10/schema}FocusOptions"/>
 *         <element name="IrCutFilterModes" type="{http://www.onvif.org/ver10/schema}IrCutFilterMode" maxOccurs="unbounded"/>
 *         <element name="Sharpness" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *         <element name="WideDynamicRange" type="{http://www.onvif.org/ver10/schema}WideDynamicRangeOptions"/>
 *         <element name="WhiteBalance" type="{http://www.onvif.org/ver10/schema}WhiteBalanceOptions"/>
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
        name = "ImagingOptions",
        propOrder = {
                "backlightCompensation",
                "brightness",
                "colorSaturation",
                "contrast",
                "exposure",
                "focus",
                "irCutFilterModes",
                "sharpness",
                "wideDynamicRange",
                "whiteBalance",
                "any"
        })
public class ImagingOptions {

    /**
     * -- GETTER --
     *  Ruft den Wert der backlightCompensation-Eigenschaft ab.
     *
     * @return possible object is {@link BacklightCompensationOptions }
     */
    @Getter @XmlElement(name = "BacklightCompensation", required = true)
    protected BacklightCompensationOptions backlightCompensation;

    /**
     * -- GETTER --
     *  Ruft den Wert der brightness-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "Brightness", required = true)
    protected FloatRange brightness;

    /**
     * -- GETTER --
     *  Ruft den Wert der colorSaturation-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "ColorSaturation", required = true)
    protected FloatRange colorSaturation;

    /**
     * -- GETTER --
     *  Ruft den Wert der contrast-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "Contrast", required = true)
    protected FloatRange contrast;

    /**
     * -- GETTER --
     *  Ruft den Wert der exposure-Eigenschaft ab.
     *
     * @return possible object is {@link ExposureOptions }
     */
    @Getter @XmlElement(name = "Exposure", required = true)
    protected ExposureOptions exposure;

    /**
     * -- GETTER --
     *  Ruft den Wert der focus-Eigenschaft ab.
     *
     * @return possible object is {@link FocusOptions }
     */
    @Getter @XmlElement(name = "Focus", required = true)
    protected FocusOptions focus;

    @XmlElement(name = "IrCutFilterModes", required = true)
    protected List<IrCutFilterMode> irCutFilterModes;

    /**
     * -- GETTER --
     *  Ruft den Wert der sharpness-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "Sharpness", required = true)
    protected FloatRange sharpness;

    /**
     * -- GETTER --
     *  Ruft den Wert der wideDynamicRange-Eigenschaft ab.
     *
     * @return possible object is {@link WideDynamicRangeOptions }
     */
    @Getter @XmlElement(name = "WideDynamicRange", required = true)
    protected WideDynamicRangeOptions wideDynamicRange;

    /**
     * -- GETTER --
     *  Ruft den Wert der whiteBalance-Eigenschaft ab.
     *
     * @return possible object is {@link WhiteBalanceOptions }
     */
    @Getter @XmlElement(name = "WhiteBalance", required = true)
    protected WhiteBalanceOptions whiteBalance;

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
     * Legt den Wert der backlightCompensation-Eigenschaft fest.
     *
     * @param value allowed object is {@link BacklightCompensationOptions }
     */
    public void setBacklightCompensation(BacklightCompensationOptions value) {
        this.backlightCompensation = value;
    }

    /**
     * Legt den Wert der brightness-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setBrightness(FloatRange value) {
        this.brightness = value;
    }

    /**
     * Legt den Wert der colorSaturation-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setColorSaturation(FloatRange value) {
        this.colorSaturation = value;
    }

    /**
     * Legt den Wert der contrast-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setContrast(FloatRange value) {
        this.contrast = value;
    }

    /**
     * Legt den Wert der exposure-Eigenschaft fest.
     *
     * @param value allowed object is {@link ExposureOptions }
     */
    public void setExposure(ExposureOptions value) {
        this.exposure = value;
    }

    /**
     * Legt den Wert der focus-Eigenschaft fest.
     *
     * @param value allowed object is {@link FocusOptions }
     */
    public void setFocus(FocusOptions value) {
        this.focus = value;
    }

    /**
     * Gets the value of the irCutFilterModes property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * irCutFilterModes property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getIrCutFilterModes().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link IrCutFilterMode }
     */
    public List<IrCutFilterMode> getIrCutFilterModes() {
        if (irCutFilterModes == null) {
            irCutFilterModes = new ArrayList<IrCutFilterMode>();
        }
        return this.irCutFilterModes;
    }

    /**
     * Legt den Wert der sharpness-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setSharpness(FloatRange value) {
        this.sharpness = value;
    }

    /**
     * Legt den Wert der wideDynamicRange-Eigenschaft fest.
     *
     * @param value allowed object is {@link WideDynamicRangeOptions }
     */
    public void setWideDynamicRange(WideDynamicRangeOptions value) {
        this.wideDynamicRange = value;
    }

    /**
     * Legt den Wert der whiteBalance-Eigenschaft fest.
     *
     * @param value allowed object is {@link WhiteBalanceOptions }
     */
    public void setWhiteBalance(WhiteBalanceOptions value) {
        this.whiteBalance = value;
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
