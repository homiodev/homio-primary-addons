package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import javax.xml.datatype.Duration;
import lombok.Getter;

/**
 * Java-Klasse f�r RelayOutputSettings complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="RelayOutputSettings">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}RelayMode"/>
 *         <element name="DelayTime" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         <element name="IdleState" type="{http://www.onvif.org/ver10/schema}RelayIdleState"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RelayOutputSettings",
        propOrder = {"mode", "delayTime", "idleState"})
public class RelayOutputSettings {

    /**
     * -- GETTER --
     *  Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link RelayMode }
     */
    @XmlElement(name = "Mode", required = true)
    protected RelayMode mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der delayTime-Eigenschaft ab.
     *
     * @return possible object is {@link Duration }
     */
    @XmlElement(name = "DelayTime", required = true)
    protected Duration delayTime;

    /**
     * -- GETTER --
     *  Ruft den Wert der idleState-Eigenschaft ab.
     *
     * @return possible object is {@link RelayIdleState }
     */
    @XmlElement(name = "IdleState", required = true)
    protected RelayIdleState idleState;

    /**
     * Legt den Wert der mode-Eigenschaft fest.
     *
     * @param value allowed object is {@link RelayMode }
     */
    public void setMode(RelayMode value) {
        this.mode = value;
    }

    /**
     * Legt den Wert der delayTime-Eigenschaft fest.
     *
     * @param value allowed object is {@link Duration }
     */
    public void setDelayTime(Duration value) {
        this.delayTime = value;
    }

    /**
     * Legt den Wert der idleState-Eigenschaft fest.
     *
     * @param value allowed object is {@link RelayIdleState }
     */
    public void setIdleState(RelayIdleState value) {
        this.idleState = value;
    }
}
