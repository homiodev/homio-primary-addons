package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PTZPresetTourOperation;

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
 *         <element name="PresetTourToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         <element name="Operation" type="{http://www.onvif.org/ver10/schema}PTZPresetTourOperation"/>
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
        propOrder = {"profileToken", "presetTourToken", "operation"})
@XmlRootElement(name = "OperatePresetTour")
public class OperatePresetTour {

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
     *  Ruft den Wert der presetTourToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "PresetTourToken", required = true)
    protected String presetTourToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der operation-Eigenschaft ab.
     *
     * @return possible object is {@link PTZPresetTourOperation }
     */
    @XmlElement(name = "Operation", required = true)
    protected PTZPresetTourOperation operation;

    /**
     * Legt den Wert der profileToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    /**
     * Legt den Wert der presetTourToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPresetTourToken(String value) {
        this.presetTourToken = value;
    }

    /**
     * Legt den Wert der operation-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZPresetTourOperation }
     */
    public void setOperation(PTZPresetTourOperation value) {
        this.operation = value;
    }
}
