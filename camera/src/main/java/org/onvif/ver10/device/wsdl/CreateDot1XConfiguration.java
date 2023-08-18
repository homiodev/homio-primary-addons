//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.Dot1XConfiguration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"dot1XConfiguration"})
@XmlRootElement(name = "CreateDot1XConfiguration")
public class CreateDot1XConfiguration {

    @XmlElement(name = "Dot1XConfiguration", required = true)
    protected Dot1XConfiguration dot1XConfiguration;

    /**
     * Ruft den Wert der dot1XConfiguration-Eigenschaft ab.
     *
     * @return possible object is {@link Dot1XConfiguration }
     */
    public Dot1XConfiguration getDot1XConfiguration() {
        return dot1XConfiguration;
    }

    /**
     * Legt den Wert der dot1XConfiguration-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot1XConfiguration }
     */
    public void setDot1XConfiguration(Dot1XConfiguration value) {
        this.dot1XConfiguration = value;
    }
}
