package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r VideoSourceConfigurationExtension complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="VideoSourceConfigurationExtension">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Rotate" type="{http://www.onvif.org/ver10/schema}Rotate" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}VideoSourceConfigurationExtension2" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoSourceConfigurationExtension",
        propOrder = {"rotate", "extension"})
public class VideoSourceConfigurationExtension {

    /**
     * -- GETTER --
     *  Ruft den Wert der rotate-Eigenschaft ab.
     *
     * @return possible object is {@link Rotate }
     */
    @XmlElement(name = "Rotate")
    protected Rotate rotate;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link VideoSourceConfigurationExtension2 }
     */
    @XmlElement(name = "Extension")
    protected VideoSourceConfigurationExtension2 extension;

    /**
     * Legt den Wert der rotate-Eigenschaft fest.
     *
     * @param value allowed object is {@link Rotate }
     */
    public void setRotate(Rotate value) {
        this.rotate = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoSourceConfigurationExtension2 }
     */
    public void setExtension(VideoSourceConfigurationExtension2 value) {
        this.extension = value;
    }
}
