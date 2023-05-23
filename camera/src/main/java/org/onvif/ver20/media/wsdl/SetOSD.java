package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.OSDConfiguration;

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
 *         &lt;element name="OSD" type="{http://www.onvif.org/ver10/schema}OSDConfiguration"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"osd"})
@XmlRootElement(name = "SetOSD")
public class SetOSD {

    @XmlElement(name = "OSD", required = true)
    protected OSDConfiguration osd;

    /**
     * Ruft den Wert der osd-Eigenschaft ab.
     *
     * @return possible object is {@link OSDConfiguration }
     */
    public OSDConfiguration getOSD() {
        return osd;
    }

    /**
     * Legt den Wert der osd-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDConfiguration }
     */
    public void setOSD(OSDConfiguration value) {
        this.osd = value;
    }
}
