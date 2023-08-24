package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.ImagingSettings20;

/**
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VideoSourceToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         &lt;element name="ImagingSettings" type="{http://www.onvif.org/ver10/schema}ImagingSettings20"/>
 *         &lt;element name="ForcePersistence" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceToken", "imagingSettings", "forcePersistence"})
@XmlRootElement(name = "SetImagingSettings")
public class SetImagingSettings {

    /**
     * -- GETTER --
     *  Ruft den Wert der videoSourceToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der imagingSettings-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingSettings20 }
     */
    @Getter @XmlElement(name = "ImagingSettings", required = true)
    protected ImagingSettings20 imagingSettings;

    @XmlElement(name = "ForcePersistence")
    protected Boolean forcePersistence;

    /**
     * Legt den Wert der videoSourceToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }

    /**
     * Legt den Wert der imagingSettings-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingSettings20 }
     */
    public void setImagingSettings(ImagingSettings20 value) {
        this.imagingSettings = value;
    }

    /**
     * Ruft den Wert der forcePersistence-Eigenschaft ab.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isForcePersistence() {
        return forcePersistence;
    }

    /**
     * Legt den Wert der forcePersistence-Eigenschaft fest.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setForcePersistence(Boolean value) {
        this.forcePersistence = value;
    }
}
