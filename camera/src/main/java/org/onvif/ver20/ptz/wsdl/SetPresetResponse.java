package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

/**
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PresetToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"presetToken"})
@XmlRootElement(name = "SetPresetResponse")
public class SetPresetResponse {

    /**
     * -- GETTER --
     *  Ruft den Wert der presetToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "PresetToken", required = true)
    protected String presetToken;

    /**
     * Legt den Wert der presetToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPresetToken(String value) {
        this.presetToken = value;
    }
}
