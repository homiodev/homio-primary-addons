package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTControlDirection",
        propOrder = {"eFlip", "reverse", "extension"})
public class PTControlDirection {

    @XmlElement(name = "EFlip")
    protected EFlip eFlip;


    @Getter @XmlElement(name = "Reverse")
    protected Reverse reverse;


    @Getter @XmlElement(name = "Extension")
    protected PTControlDirectionExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public EFlip getEFlip() {
        return eFlip;
    }


    public void setEFlip(EFlip value) {
        this.eFlip = value;
    }


    public void setReverse(Reverse value) {
        this.reverse = value;
    }


    public void setExtension(PTControlDirectionExtension value) {
        this.extension = value;
    }

}
