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
 *         <element name="ProfileToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         <element name="PanTilt" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="Zoom" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "panTilt", "zoom"})
@XmlRootElement(name = "Stop")
public class Stop {

    /**
     * -- GETTER --
     *  Ruft den Wert der profileToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    @XmlElement(name = "PanTilt")
    protected Boolean panTilt;

    @XmlElement(name = "Zoom")
    protected Boolean zoom;

    /**
     * Legt den Wert der profileToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    /**
     * Ruft den Wert der panTilt-Eigenschaft ab.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isPanTilt() {
        return panTilt;
    }

    /**
     * Legt den Wert der panTilt-Eigenschaft fest.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setPanTilt(Boolean value) {
        this.panTilt = value;
    }

    /**
     * Ruft den Wert der zoom-Eigenschaft ab.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isZoom() {
        return zoom;
    }

    /**
     * Legt den Wert der zoom-Eigenschaft fest.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setZoom(Boolean value) {
        this.zoom = value;
    }
}
