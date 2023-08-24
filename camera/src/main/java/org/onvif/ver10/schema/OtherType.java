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
        name = "OtherType",
        propOrder = {"type", "likelihood", "any"})
public class OtherType {


    @XmlElement(name = "Type", required = true)
    protected String type;


    @Getter @XmlElement(name = "Likelihood")
    protected float likelihood;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setType(String value) {
        this.type = value;
    }


    public void setLikelihood(float value) {
        this.likelihood = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
