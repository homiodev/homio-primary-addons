package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r ImagingOptions20Extension2 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="ImagingOptions20Extension2">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="IrCutFilterAutoAdjustment" type="{http://www.onvif.org/ver10/schema}IrCutFilterAutoAdjustmentOptions" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}ImagingOptions20Extension3" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ImagingOptions20Extension2",
        propOrder = {"irCutFilterAutoAdjustment", "extension"})
public class ImagingOptions20Extension2 {

    /**
     * -- GETTER --
     *  Ruft den Wert der irCutFilterAutoAdjustment-Eigenschaft ab.
     *
     * @return possible object is {@link IrCutFilterAutoAdjustmentOptions }
     */
    @XmlElement(name = "IrCutFilterAutoAdjustment")
    protected IrCutFilterAutoAdjustmentOptions irCutFilterAutoAdjustment;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ImagingOptions20Extension3 }
     */
    @XmlElement(name = "Extension")
    protected ImagingOptions20Extension3 extension;

    /**
     * Legt den Wert der irCutFilterAutoAdjustment-Eigenschaft fest.
     *
     * @param value allowed object is {@link IrCutFilterAutoAdjustmentOptions }
     */
    public void setIrCutFilterAutoAdjustment(IrCutFilterAutoAdjustmentOptions value) {
        this.irCutFilterAutoAdjustment = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ImagingOptions20Extension3 }
     */
    public void setExtension(ImagingOptions20Extension3 value) {
        this.extension = value;
    }
}
