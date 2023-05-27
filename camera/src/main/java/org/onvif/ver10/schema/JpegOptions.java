package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r JpegOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="JpegOptions">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ResolutionsAvailable" type="{http://www.onvif.org/ver10/schema}VideoResolution" maxOccurs="unbounded"/>
 *         <element name="FrameRateRange" type="{http://www.onvif.org/ver10/schema}IntRange"/>
 *         <element name="EncodingIntervalRange" type="{http://www.onvif.org/ver10/schema}IntRange"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "JpegOptions",
    propOrder = {"resolutionsAvailable", "frameRateRange", "encodingIntervalRange"})
@XmlSeeAlso({JpegOptions2.class})
public class JpegOptions {

    @XmlElement(name = "ResolutionsAvailable", required = true)
    protected List<VideoResolution> resolutionsAvailable;

    @XmlElement(name = "FrameRateRange", required = true)
    protected IntRange frameRateRange;

    @XmlElement(name = "EncodingIntervalRange", required = true)
    protected IntRange encodingIntervalRange;

    /**
     * Gets the value of the resolutionsAvailable property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * resolutionsAvailable property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getResolutionsAvailable().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link VideoResolution }
     */
    public List<VideoResolution> getResolutionsAvailable() {
        if (resolutionsAvailable == null) {
            resolutionsAvailable = new ArrayList<VideoResolution>();
        }
        return this.resolutionsAvailable;
    }

    /**
     * Ruft den Wert der frameRateRange-Eigenschaft ab.
     *
     * @return possible object is {@link IntRange }
     */
    public IntRange getFrameRateRange() {
        return frameRateRange;
    }

    /**
     * Legt den Wert der frameRateRange-Eigenschaft fest.
     *
     * @param value allowed object is {@link IntRange }
     */
    public void setFrameRateRange(IntRange value) {
        this.frameRateRange = value;
    }

    /**
     * Ruft den Wert der encodingIntervalRange-Eigenschaft ab.
     *
     * @return possible object is {@link IntRange }
     */
    public IntRange getEncodingIntervalRange() {
        return encodingIntervalRange;
    }

    /**
     * Legt den Wert der encodingIntervalRange-Eigenschaft fest.
     *
     * @param value allowed object is {@link IntRange }
     */
    public void setEncodingIntervalRange(IntRange value) {
        this.encodingIntervalRange = value;
    }
}
