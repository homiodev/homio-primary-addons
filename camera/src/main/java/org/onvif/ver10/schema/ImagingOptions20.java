package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r ImagingOptions20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="ImagingOptions20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="BacklightCompensation" type="{http://www.onvif.org/ver10/schema}BacklightCompensationOptions20" minOccurs="0"/>
 *         <element name="Brightness" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/>
 *         <element name="ColorSaturation" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/>
 *         <element name="Contrast" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/>
 *         <element name="Exposure" type="{http://www.onvif.org/ver10/schema}ExposureOptions20" minOccurs="0"/>
 *         <element name="Focus" type="{http://www.onvif.org/ver10/schema}FocusOptions20" minOccurs="0"/>
 *         <element name="IrCutFilterModes" type="{http://www.onvif.org/ver10/schema}IrCutFilterMode" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="Sharpness" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/>
 *         <element name="WideDynamicRange" type="{http://www.onvif.org/ver10/schema}WideDynamicRangeOptions20" minOccurs="0"/>
 *         <element name="WhiteBalance" type="{http://www.onvif.org/ver10/schema}WhiteBalanceOptions20" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}ImagingOptions20Extension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ImagingOptions20",
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
                "extension"
        })
public class ImagingOptions20 {

    /**
     * -- GETTER --
     *  Ruft den Wert der backlightCompensation-Eigenschaft ab.
     *
     * @return possible object is {@link BacklightCompensationOptions20 }
     */
    @Getter @XmlElement(name = "BacklightCompensation")
    protected BacklightCompensationOptions20 backlightCompensation;

    /**
     * -- GETTER --
     *  Ruft den Wert der brightness-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "Brightness")
    protected FloatRange brightness;

    /**
     * -- GETTER --
     *  Ruft den Wert der colorSaturation-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "ColorSaturation")
    protected FloatRange colorSaturation;

    /**
     * -- GETTER --
     *  Ruft den Wert der contrast-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "Contrast")
    protected FloatRange contrast;

    /**
     * -- GETTER --
     *  Ruft den Wert der exposure-Eigenschaft ab.
     *
     * @return possible object is {@link ExposureOptions20 }
     */
    @Getter @XmlElement(name = "Exposure")
    protected ExposureOptions20 exposure;

    /**
     * -- GETTER --
     *  Ruft den Wert der focus-Eigenschaft ab.
     *
     * @return possible object is {@link FocusOptions20 }
     */
    @Getter @XmlElement(name = "Focus")
    protected FocusOptions20 focus;

    @XmlElement(name = "IrCutFilterModes")
    protected List<IrCutFilterMode> irCutFilterModes;

    /**
     * -- GETTER --
     *  Ruft den Wert der sharpness-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "Sharpness")
    protected FloatRange sharpness;

    /**
     * -- GETTER --
     *  Ruft den Wert der wideDynamicRange-Eigenschaft ab.
     *
     * @return possible object is {@link WideDynamicRangeOptions20 }
     */
    @Getter @XmlElement(name = "WideDynamicRange")
    protected WideDynamicRangeOptions20 wideDynamicRange;

    /**
     * -- GETTER --
     *  Ruft den Wert der whiteBalance-Eigenschaft ab.
     *
     * @return possible object is {@link WhiteBalanceOptions20 }
     */
    @Getter @XmlElement(name = "WhiteBalance")
    protected WhiteBalanceOptions20 whiteBalance;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingOptions20Extension }
     */
    @Getter @XmlElement(name = "Extension")
    protected ImagingOptions20Extension extension;

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
     * @param value allowed object is {@link BacklightCompensationOptions20 }
     */
    public void setBacklightCompensation(BacklightCompensationOptions20 value) {
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
     * @param value allowed object is {@link ExposureOptions20 }
     */
    public void setExposure(ExposureOptions20 value) {
        this.exposure = value;
    }

    /**
     * Legt den Wert der focus-Eigenschaft fest.
     *
     * @param value allowed object is {@link FocusOptions20 }
     */
    public void setFocus(FocusOptions20 value) {
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
     * @param value allowed object is {@link WideDynamicRangeOptions20 }
     */
    public void setWideDynamicRange(WideDynamicRangeOptions20 value) {
        this.wideDynamicRange = value;
    }

    /**
     * Legt den Wert der whiteBalance-Eigenschaft fest.
     *
     * @param value allowed object is {@link WhiteBalanceOptions20 }
     */
    public void setWhiteBalance(WhiteBalanceOptions20 value) {
        this.whiteBalance = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingOptions20Extension }
     */
    public void setExtension(ImagingOptions20Extension value) {
        this.extension = value;
    }

}
