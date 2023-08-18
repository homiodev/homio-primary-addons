package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Java-Klasse f�r ImagingSettingsExtension20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="ImagingSettingsExtension20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="ImageStabilization" type="{http://www.onvif.org/ver10/schema}ImageStabilization" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}ImagingSettingsExtension202" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ImagingSettingsExtension20",
        propOrder = {"any", "imageStabilization", "extension"})
public class ImagingSettingsExtension20 {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "ImageStabilization")
    protected ImageStabilization imageStabilization;

    @XmlElement(name = "Extension")
    protected ImagingSettingsExtension202 extension;

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

    /**
     * Ruft den Wert der imageStabilization-Eigenschaft ab.
     *
     * @return possible object is {@link ImageStabilization }
     */
    public ImageStabilization getImageStabilization() {
        return imageStabilization;
    }

    /**
     * Legt den Wert der imageStabilization-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImageStabilization }
     */
    public void setImageStabilization(ImageStabilization value) {
        this.imageStabilization = value;
    }

    /**
     * Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingSettingsExtension202 }
     */
    public ImagingSettingsExtension202 getExtension() {
        return extension;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingSettingsExtension202 }
     */
    public void setExtension(ImagingSettingsExtension202 value) {
        this.extension = value;
    }
}
