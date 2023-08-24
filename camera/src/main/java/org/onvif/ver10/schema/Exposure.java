package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r Exposure complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Exposure">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}ExposureMode"/>
 *         <element name="Priority" type="{http://www.onvif.org/ver10/schema}ExposurePriority"/>
 *         <element name="Window" type="{http://www.onvif.org/ver10/schema}Rectangle"/>
 *         <element name="MinExposureTime" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="MaxExposureTime" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="MinGain" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="MaxGain" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="MinIris" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="MaxIris" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="ExposureTime" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="Gain" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="Iris" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Exposure",
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
public class Exposure {

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
    @XmlElement(name = "Priority", required = true)
    protected ExposurePriority priority;

    /**
     * -- GETTER --
     *  Ruft den Wert der window-Eigenschaft ab.
     *
     * @return possible object is {@link Rectangle }
     */
    @XmlElement(name = "Window", required = true)
    protected Rectangle window;

    /**
     * -- GETTER --
     *  Ruft den Wert der minExposureTime-Eigenschaft ab.
     */
    @XmlElement(name = "MinExposureTime")
    protected float minExposureTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der maxExposureTime-Eigenschaft ab.
     */
    @XmlElement(name = "MaxExposureTime")
    protected float maxExposureTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der minGain-Eigenschaft ab.
     */
    @XmlElement(name = "MinGain")
    protected float minGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der maxGain-Eigenschaft ab.
     */
    @XmlElement(name = "MaxGain")
    protected float maxGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der minIris-Eigenschaft ab.
     */
    @XmlElement(name = "MinIris")
    protected float minIris;

    /**
     * -- GETTER --
     *  Ruft den Wert der maxIris-Eigenschaft ab.
     */
    @XmlElement(name = "MaxIris")
    protected float maxIris;

    /**
     * -- GETTER --
     *  Ruft den Wert der exposureTime-Eigenschaft ab.
     */
    @XmlElement(name = "ExposureTime")
    protected float exposureTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der gain-Eigenschaft ab.
     */
    @XmlElement(name = "Gain")
    protected float gain;

    /**
     * -- GETTER --
     *  Ruft den Wert der iris-Eigenschaft ab.
     */
    @XmlElement(name = "Iris")
    protected float iris;

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
     */
    public void setMinExposureTime(float value) {
        this.minExposureTime = value;
    }

    /**
     * Legt den Wert der maxExposureTime-Eigenschaft fest.
     */
    public void setMaxExposureTime(float value) {
        this.maxExposureTime = value;
    }

    /**
     * Legt den Wert der minGain-Eigenschaft fest.
     */
    public void setMinGain(float value) {
        this.minGain = value;
    }

    /**
     * Legt den Wert der maxGain-Eigenschaft fest.
     */
    public void setMaxGain(float value) {
        this.maxGain = value;
    }

    /**
     * Legt den Wert der minIris-Eigenschaft fest.
     */
    public void setMinIris(float value) {
        this.minIris = value;
    }

    /**
     * Legt den Wert der maxIris-Eigenschaft fest.
     */
    public void setMaxIris(float value) {
        this.maxIris = value;
    }

    /**
     * Legt den Wert der exposureTime-Eigenschaft fest.
     */
    public void setExposureTime(float value) {
        this.exposureTime = value;
    }

    /**
     * Legt den Wert der gain-Eigenschaft fest.
     */
    public void setGain(float value) {
        this.gain = value;
    }

    /**
     * Legt den Wert der iris-Eigenschaft fest.
     */
    public void setIris(float value) {
        this.iris = value;
    }
}
