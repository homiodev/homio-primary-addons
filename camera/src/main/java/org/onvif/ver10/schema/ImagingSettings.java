package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r ImagingSettings complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="ImagingSettings">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="BacklightCompensation" type="{http://www.onvif.org/ver10/schema}BacklightCompensation" minOccurs="0"/>
 *         <element name="Brightness" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="ColorSaturation" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="Contrast" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="Exposure" type="{http://www.onvif.org/ver10/schema}Exposure" minOccurs="0"/>
 *         <element name="Focus" type="{http://www.onvif.org/ver10/schema}FocusConfiguration" minOccurs="0"/>
 *         <element name="IrCutFilter" type="{http://www.onvif.org/ver10/schema}IrCutFilterMode" minOccurs="0"/>
 *         <element name="Sharpness" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="WideDynamicRange" type="{http://www.onvif.org/ver10/schema}WideDynamicRange" minOccurs="0"/>
 *         <element name="WhiteBalance" type="{http://www.onvif.org/ver10/schema}WhiteBalance" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}ImagingSettingsExtension" minOccurs="0"/>
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
        name = "ImagingSettings",
        propOrder = {
                "backlightCompensation",
                "brightness",
                "colorSaturation",
                "contrast",
                "exposure",
                "focus",
                "irCutFilter",
                "sharpness",
                "wideDynamicRange",
                "whiteBalance",
                "extension"
        })
public class ImagingSettings {

    /**
     * -- GETTER --
     *  Ruft den Wert der backlightCompensation-Eigenschaft ab.
     *
     * @return possible object is {@link BacklightCompensation }
     */
    @XmlElement(name = "BacklightCompensation")
    protected BacklightCompensation backlightCompensation;

    /**
     * -- GETTER --
     *  Ruft den Wert der brightness-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Brightness")
    protected Float brightness;

    /**
     * -- GETTER --
     *  Ruft den Wert der colorSaturation-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "ColorSaturation")
    protected Float colorSaturation;

    /**
     * -- GETTER --
     *  Ruft den Wert der contrast-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Contrast")
    protected Float contrast;

    /**
     * -- GETTER --
     *  Ruft den Wert der exposure-Eigenschaft ab.
     *
     * @return possible object is {@link Exposure }
     */
    @XmlElement(name = "Exposure")
    protected Exposure exposure;

    /**
     * -- GETTER --
     *  Ruft den Wert der focus-Eigenschaft ab.
     *
     * @return possible object is {@link FocusConfiguration }
     */
    @XmlElement(name = "Focus")
    protected FocusConfiguration focus;

    /**
     * -- GETTER --
     *  Ruft den Wert der irCutFilter-Eigenschaft ab.
     *
     * @return possible object is {@link IrCutFilterMode }
     */
    @XmlElement(name = "IrCutFilter")
    protected IrCutFilterMode irCutFilter;

    /**
     * -- GETTER --
     *  Ruft den Wert der sharpness-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Sharpness")
    protected Float sharpness;

    /**
     * -- GETTER --
     *  Ruft den Wert der wideDynamicRange-Eigenschaft ab.
     *
     * @return possible object is {@link WideDynamicRange }
     */
    @XmlElement(name = "WideDynamicRange")
    protected WideDynamicRange wideDynamicRange;

    /**
     * -- GETTER --
     *  Ruft den Wert der whiteBalance-Eigenschaft ab.
     *
     * @return possible object is {@link WhiteBalance }
     */
    @XmlElement(name = "WhiteBalance")
    protected WhiteBalance whiteBalance;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingSettingsExtension }
     */
    @XmlElement(name = "Extension")
    protected ImagingSettingsExtension extension;

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
     * Legt den Wert der backlightCompensation-Eigenschaft fest.
     *
     * @param value allowed object is {@link BacklightCompensation }
     */
    public void setBacklightCompensation(BacklightCompensation value) {
        this.backlightCompensation = value;
    }

    /**
     * Legt den Wert der brightness-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setBrightness(Float value) {
        this.brightness = value;
    }

    /**
     * Legt den Wert der colorSaturation-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setColorSaturation(Float value) {
        this.colorSaturation = value;
    }

    /**
     * Legt den Wert der contrast-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setContrast(Float value) {
        this.contrast = value;
    }

    /**
     * Legt den Wert der exposure-Eigenschaft fest.
     *
     * @param value allowed object is {@link Exposure }
     */
    public void setExposure(Exposure value) {
        this.exposure = value;
    }

    /**
     * Legt den Wert der focus-Eigenschaft fest.
     *
     * @param value allowed object is {@link FocusConfiguration }
     */
    public void setFocus(FocusConfiguration value) {
        this.focus = value;
    }

    /**
     * Legt den Wert der irCutFilter-Eigenschaft fest.
     *
     * @param value allowed object is {@link IrCutFilterMode }
     */
    public void setIrCutFilter(IrCutFilterMode value) {
        this.irCutFilter = value;
    }

    /**
     * Legt den Wert der sharpness-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setSharpness(Float value) {
        this.sharpness = value;
    }

    /**
     * Legt den Wert der wideDynamicRange-Eigenschaft fest.
     *
     * @param value allowed object is {@link WideDynamicRange }
     */
    public void setWideDynamicRange(WideDynamicRange value) {
        this.wideDynamicRange = value;
    }

    /**
     * Legt den Wert der whiteBalance-Eigenschaft fest.
     *
     * @param value allowed object is {@link WhiteBalance }
     */
    public void setWhiteBalance(WhiteBalance value) {
        this.whiteBalance = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingSettingsExtension }
     */
    public void setExtension(ImagingSettingsExtension value) {
        this.extension = value;
    }

}
