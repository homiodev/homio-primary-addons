//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.w3._2005._05.xmlmime;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "base64Binary",
        propOrder = {"value"})
public class Base64Binary {


    @XmlValue
    protected byte[] value;


    @XmlAttribute(name = "contentType", namespace = "http://www.w3.org/2005/05/xmlmime")
    protected String contentType;


    public void setValue(byte[] value) {
        this.value = value;
    }


    public void setContentType(String value) {
        this.contentType = value;
    }
}
