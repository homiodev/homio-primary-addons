package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "SourceReference",
        propOrder = {"token", "any"})
public class SourceReference {


    @XmlElement(name = "Token", required = true)
    protected String token;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anyURI")
    protected String type;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setToken(String value) {
        this.token = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public String getType() {
        if (type == null) {
            return "http://www.onvif.org/ver10/schema/Receiver";
        } else {
            return type;
        }
    }


    public void setType(String value) {
        this.type = value;
    }

}
