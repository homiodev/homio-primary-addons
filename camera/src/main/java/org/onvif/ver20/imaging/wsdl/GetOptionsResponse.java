package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.ImagingOptions20;

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
 *         &lt;element name="ImagingOptions" type="{http://www.onvif.org/ver10/schema}ImagingOptions20"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"imagingOptions"})
@XmlRootElement(name = "GetOptionsResponse")
public class GetOptionsResponse {

    @XmlElement(name = "ImagingOptions", required = true)
    protected ImagingOptions20 imagingOptions;

    /**
     * Ruft den Wert der imagingOptions-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingOptions20 }
     */
    public ImagingOptions20 getImagingOptions() {
        return imagingOptions;
    }

    /**
     * Legt den Wert der imagingOptions-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingOptions20 }
     */
    public void setImagingOptions(ImagingOptions20 value) {
        this.imagingOptions = value;
    }
}