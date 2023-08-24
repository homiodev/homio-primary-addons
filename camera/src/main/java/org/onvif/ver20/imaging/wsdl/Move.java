package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.FocusMove;

/**
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VideoSourceToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         &lt;element name="Focus" type="{http://www.onvif.org/ver10/schema}FocusMove"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"videoSourceToken", "focus"})
@XmlRootElement(name = "Move")
public class Move {

    /**
     * -- GETTER --
     *  Ruft den Wert der videoSourceToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der focus-Eigenschaft ab.
     *
     * @return possible object is {@link FocusMove }
     */
    @XmlElement(name = "Focus", required = true)
    protected FocusMove focus;

    /**
     * Legt den Wert der videoSourceToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }

    /**
     * Legt den Wert der focus-Eigenschaft fest.
     *
     * @param value allowed object is {@link FocusMove }
     */
    public void setFocus(FocusMove value) {
        this.focus = value;
    }
}
