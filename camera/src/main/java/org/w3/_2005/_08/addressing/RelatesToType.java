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


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RelatesToType",
        propOrder = {"value"})
public class RelatesToType {


    @Getter @XmlValue
    @XmlSchemaType(name = "anyURI")
    protected String value;

    @XmlAttribute(name = "RelationshipType")
    protected String relationshipType;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setValue(String value) {
        this.value = value;
    }


    public String getRelationshipType() {
        if (relationshipType == null) {
            return "http://www.w3.org/2005/08/addressing/reply";
        } else {
            return relationshipType;
        }
    }


    public void setRelationshipType(String value) {
        this.relationshipType = value;
    }

}
