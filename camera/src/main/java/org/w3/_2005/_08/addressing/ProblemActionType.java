







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


    public void setSoapAction(String value) {
        this.soapAction = value;
    }

}
