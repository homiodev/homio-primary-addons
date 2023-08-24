package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "SourceIdentification",
        propOrder = {"name", "token", "extension"})
public class SourceIdentification {


    @Getter @XmlElement(name = "Name", required = true)
    protected String name;

    @XmlElement(name = "Token", required = true)
    protected List<String> token;


    @Getter @XmlElement(name = "Extension")
    protected SourceIdentificationExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setName(String value) {
        this.name = value;
    }


    public List<String> getToken() {
        if (token == null) {
            token = new ArrayList<String>();
        }
        return this.token;
    }


    public void setExtension(SourceIdentificationExtension value) {
        this.extension = value;
    }

}
