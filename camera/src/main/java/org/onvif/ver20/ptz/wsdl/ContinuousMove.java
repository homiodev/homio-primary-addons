package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PTZSpeed;

import javax.xml.datatype.Duration;

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
 *         <element name="Velocity" type="{http://www.onvif.org/ver10/schema}PTZSpeed"/>
 *         <element name="Timeout" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
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
        propOrder = {"profileToken", "velocity", "timeout"})
@XmlRootElement(name = "ContinuousMove")
public class ContinuousMove {

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
     *  Ruft den Wert der velocity-Eigenschaft ab.
     *
     * @return possible object is {@link PTZSpeed }
     */
    @XmlElement(name = "Velocity", required = true)
    protected PTZSpeed velocity;

    /**
     * -- GETTER --
     *  Ruft den Wert der timeout-Eigenschaft ab.
     *
     * @return possible object is {@link Duration }
     */
    @XmlElement(name = "Timeout")
    protected Duration timeout;

    /**
     * Legt den Wert der profileToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    /**
     * Legt den Wert der velocity-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZSpeed }
     */
    public void setVelocity(PTZSpeed value) {
        this.velocity = value;
    }

    /**
     * Legt den Wert der timeout-Eigenschaft fest.
     *
     * @param value allowed object is {@link Duration }
     */
    public void setTimeout(Duration value) {
        this.timeout = value;
    }
}
