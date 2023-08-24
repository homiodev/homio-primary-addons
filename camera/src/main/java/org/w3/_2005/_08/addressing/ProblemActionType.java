//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.w3._2005._08.addressing;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ProblemActionType",
        propOrder = {"action", "soapAction"})
public class ProblemActionType {


    @XmlElement(name = "Action")
    protected AttributedURIType action;


    @XmlElement(name = "SoapAction")
    @XmlSchemaType(name = "anyURI")
    protected String soapAction;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAction(AttributedURIType value) {
        this.action = value;
    }

    /**
     * Legt den Wert der soapAction-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setSoapAction(String value) {
        this.soapAction = value;
    }

}
