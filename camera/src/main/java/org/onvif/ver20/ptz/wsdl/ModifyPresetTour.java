package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PresetTour;

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
 *         <element name="ProfileToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         <element name="PresetTour" type="{http://www.onvif.org/ver10/schema}PresetTour"/>
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
        propOrder = {"profileToken", "presetTour"})
@XmlRootElement(name = "ModifyPresetTour")
public class ModifyPresetTour {

    /**
     * -- GETTER --
     *  Ruft den Wert der profileToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der presetTour-Eigenschaft ab.
     *
     * @return possible object is {@link PresetTour }
     */
    @XmlElement(name = "PresetTour", required = true)
    protected PresetTour presetTour;

    /**
     * Legt den Wert der profileToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    /**
     * Legt den Wert der presetTour-Eigenschaft fest.
     *
     * @param value allowed object is {@link PresetTour }
     */
    public void setPresetTour(PresetTour value) {
        this.presetTour = value;
    }
}
