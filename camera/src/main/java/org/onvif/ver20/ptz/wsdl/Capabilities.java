package org.onvif.ver20.ptz.wsdl;

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

    @XmlAttribute(name = "EFlip")
    protected Boolean eFlip;

    @XmlAttribute(name = "Reverse")
    protected Boolean reverse;

    @XmlAttribute(name = "GetCompatibleConfigurations")
    protected Boolean getCompatibleConfigurations;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    
    public Boolean isEFlip() {
        return eFlip;
    }

    
    public void setEFlip(Boolean value) {
        this.eFlip = value;
    }

    
    public Boolean isReverse() {
        return reverse;
    }

    
    public void setReverse(Boolean value) {
        this.reverse = value;
    }

    
    public Boolean isGetCompatibleConfigurations() {
        return getCompatibleConfigurations;
    }

    
    public void setGetCompatibleConfigurations(Boolean value) {
        this.getCompatibleConfigurations = value;
    }

}
