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
        name = "Receiver",
        propOrder = {"token", "configuration", "any"})
public class Receiver {


    @XmlElement(name = "Token", required = true)
    protected String token;


    @Getter @XmlElement(name = "Configuration", required = true)
    protected ReceiverConfiguration configuration;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setToken(String value) {
        this.token = value;
    }


    public void setConfiguration(ReceiverConfiguration value) {
        this.configuration = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
