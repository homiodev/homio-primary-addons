package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTControlDirectionOptions",
        propOrder = {"eFlip", "reverse", "extension"})
public class PTControlDirectionOptions {

    @XmlElement(name = "EFlip")
    protected EFlipOptions eFlip;


    @Getter @XmlElement(name = "Reverse")
    protected ReverseOptions reverse;


    @Getter @XmlElement(name = "Extension")
    protected PTControlDirectionOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public EFlipOptions getEFlip() {
        return eFlip;
    }


    public void setEFlip(EFlipOptions value) {
        this.eFlip = value;
    }


    public void setReverse(ReverseOptions value) {
        this.reverse = value;
    }


    public void setExtension(PTControlDirectionOptionsExtension value) {
        this.extension = value;
    }

}
