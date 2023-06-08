package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
 *         <element name="PresetName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="PresetToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"profileToken", "presetName", "presetToken"})
@XmlRootElement(name = "SetPreset")
public class SetPreset {

    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    @XmlElement(name = "PresetName")
    protected String presetName;

    @XmlElement(name = "PresetToken")
    protected String presetToken;

    /**
     * Ruft den Wert der profileToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getProfileToken() {
        return profileToken;
    }

    /**
     * Legt den Wert der profileToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    /**
     * Ruft den Wert der presetName-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getPresetName() {
        return presetName;
    }

    /**
     * Legt den Wert der presetName-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPresetName(String value) {
        this.presetName = value;
    }

    /**
     * Ruft den Wert der presetToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getPresetToken() {
        return presetToken;
    }

    /**
     * Legt den Wert der presetToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPresetToken(String value) {
        this.presetToken = value;
    }
}