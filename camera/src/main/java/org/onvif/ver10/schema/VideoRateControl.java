package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r VideoRateControl complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="VideoRateControl">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="FrameRateLimit" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="EncodingInterval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="BitrateLimit" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoRateControl",
        propOrder = {"frameRateLimit", "encodingInterval", "bitrateLimit"})
public class VideoRateControl {

    /**
     * -- GETTER --
     *  Ruft den Wert der frameRateLimit-Eigenschaft ab.
     */
    @XmlElement(name = "FrameRateLimit")
    protected int frameRateLimit;

    /**
     * -- GETTER --
     *  Ruft den Wert der encodingInterval-Eigenschaft ab.
     */
    @XmlElement(name = "EncodingInterval")
    protected int encodingInterval;

    /**
     * -- GETTER --
     *  Ruft den Wert der bitrateLimit-Eigenschaft ab.
     */
    @XmlElement(name = "BitrateLimit")
    protected int bitrateLimit;

    /**
     * Legt den Wert der frameRateLimit-Eigenschaft fest.
     */
    public void setFrameRateLimit(int value) {
        this.frameRateLimit = value;
    }

    /**
     * Legt den Wert der encodingInterval-Eigenschaft fest.
     */
    public void setEncodingInterval(int value) {
        this.encodingInterval = value;
    }

    /**
     * Legt den Wert der bitrateLimit-Eigenschaft fest.
     */
    public void setBitrateLimit(int value) {
        this.bitrateLimit = value;
    }
}
