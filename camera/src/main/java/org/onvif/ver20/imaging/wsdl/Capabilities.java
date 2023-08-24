package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Capabilities",
        propOrder = {"any"})
public class Capabilities {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlAttribute(name = "ImageStabilization")
    protected Boolean imageStabilization;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    
    public Boolean isImageStabilization() {
        return imageStabilization;
    }

    
    public void setImageStabilization(Boolean value) {
        this.imageStabilization = value;
    }

}
