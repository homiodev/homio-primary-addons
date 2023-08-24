package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

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
 *         &lt;element name="Reboot" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        propOrder = {"reboot"})
@XmlRootElement(name = "SetVideoSourceModeResponse")
public class SetVideoSourceModeResponse {

    /**
     * -- GETTER --
     *  Ruft den Wert der reboot-Eigenschaft ab.
     */
    @XmlElement(name = "Reboot")
    protected boolean reboot;

    /**
     * Legt den Wert der reboot-Eigenschaft fest.
     */
    public void setReboot(boolean value) {
        this.reboot = value;
    }
}
