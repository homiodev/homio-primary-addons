package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Java-Klasse f�r PTZMoveStatus complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PTZMoveStatus">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PanTilt" type="{http://www.onvif.org/ver10/schema}MoveStatus" minOccurs="0"/>
 *         <element name="Zoom" type="{http://www.onvif.org/ver10/schema}MoveStatus" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZMoveStatus",
        propOrder = {"panTilt", "zoom"})
public class PTZMoveStatus {

    /**
     * -- GETTER --
     *  Ruft den Wert der panTilt-Eigenschaft ab.
     *
     * @return possible object is {@link MoveStatus }
     */
    @XmlElement(name = "PanTilt")
    protected MoveStatus panTilt;

    /**
     * -- GETTER --
     *  Ruft den Wert der zoom-Eigenschaft ab.
     *
     * @return possible object is {@link MoveStatus }
     */
    @XmlElement(name = "Zoom")
    protected MoveStatus zoom;

    /**
     * Legt den Wert der panTilt-Eigenschaft fest.
     *
     * @param value allowed object is {@link MoveStatus }
     */
    public void setPanTilt(MoveStatus value) {
        this.panTilt = value;
    }

    /**
     * Legt den Wert der zoom-Eigenschaft fest.
     *
     * @param value allowed object is {@link MoveStatus }
     */
    public void setZoom(MoveStatus value) {
        this.zoom = value;
    }
}
