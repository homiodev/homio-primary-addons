package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Type describing the exposure settings.
 *
 * <p>Java-Klasse f�r Exposure20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Exposure20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}ExposureMode"/>
 *         <element name="Priority" type="{http://www.onvif.org/ver10/schema}ExposurePriority" minOccurs="0"/>
 *         <element name="Window" type="{http://www.onvif.org/ver10/schema}Rectangle" minOccurs="0"/>
 *         <element name="MinExposureTime" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="MaxExposureTime" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="MinGain" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="MaxGain" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="MinIris" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="MaxIris" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="ExposureTime" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="Gain" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         <element name="Iris" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Exposure20",
        propOrder = {
                "mode",
                "priority",
                "window",
                "minExposureTime",
                "maxExposureTime",
                "minGain",
                "maxGain",
                "minIris",
                "maxIris",
                "exposureTime",
                "gain",
                "iris"
        })
public class Exposure20 {

    /**
     * -- GETTER --
     *  Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link ExposureMode }
     */
    @XmlElement(name = "Mode", required = true)
    protected ExposureMode mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der priority-Eigenschaft ab.
     *
     * @return possible object is {@link ExposurePriority }
     */
    @XmlElement(name = "Priority")
    protected ExposurePriority priority;

    /**
     * -- GETTER --
     *  Ruft den Wert der window-Eigenschaft ab.
     *
     * @return possible object is {@link Rectangle }
     */
    @XmlElement(name = "Window")
    protected Rectangle window;

    /**
     * -- GETTER --
     *  Ruft den Wert der minExposureTime-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "MinExposureTime")
    protected Float minExposureTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der maxExposureTime-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "MaxExposureTime")
    protected Float maxExposureTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der minGain-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "MinGain")
    protected Float minGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der maxGain-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "MaxGain")
    protected Float maxGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der minIris-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "MinIris")
    protected Float minIris;

    /**
     * -- GETTER --
     *  Ruft den Wert der maxIris-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "MaxIris")
    protected Float maxIris;

    /**
     * -- GETTER --
     *  Ruft den Wert der exposureTime-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "ExposureTime")
    protected Float exposureTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der gain-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Gain")
    protected Float gain;

    /**
     * -- GETTER --
     *  Ruft den Wert der iris-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Iris")
    protected Float iris;

    /**
     * Legt den Wert der mode-Eigenschaft fest.
     *
     * @param value allowed object is {@link ExposureMode }
     */
    public void setMode(ExposureMode value) {
        this.mode = value;
    }

    /**
     * Legt den Wert der priority-Eigenschaft fest.
     *
     * @param value allowed object is {@link ExposurePriority }
     */
    public void setPriority(ExposurePriority value) {
        this.priority = value;
    }

    /**
     * Legt den Wert der window-Eigenschaft fest.
     *
     * @param value allowed object is {@link Rectangle }
     */
    public void setWindow(Rectangle value) {
        this.window = value;
    }

    /**
     * Legt den Wert der minExposureTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setMinExposureTime(Float value) {
        this.minExposureTime = value;
    }

    /**
     * Legt den Wert der maxExposureTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setMaxExposureTime(Float value) {
        this.maxExposureTime = value;
    }

    /**
     * Legt den Wert der minGain-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setMinGain(Float value) {
        this.minGain = value;
    }

    /**
     * Legt den Wert der maxGain-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setMaxGain(Float value) {
        this.maxGain = value;
    }

    /**
     * Legt den Wert der minIris-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setMinIris(Float value) {
        this.minIris = value;
    }

    /**
     * Legt den Wert der maxIris-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setMaxIris(Float value) {
        this.maxIris = value;
    }

    /**
     * Legt den Wert der exposureTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setExposureTime(Float value) {
        this.exposureTime = value;
    }

    /**
     * Legt den Wert der gain-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setGain(Float value) {
        this.gain = value;
    }

    /**
     * Legt den Wert der iris-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setIris(Float value) {
        this.iris = value;
    }
}
