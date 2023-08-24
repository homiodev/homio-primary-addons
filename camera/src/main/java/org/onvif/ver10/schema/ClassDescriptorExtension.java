package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ClassDescriptorExtension",
        propOrder = {"any", "otherTypes", "extension"})
public class ClassDescriptorExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "OtherTypes", required = true)
    protected List<OtherType> otherTypes;

    
    @Getter @XmlElement(name = "Extension")
    protected ClassDescriptorExtension2 extension;

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

    
    public List<OtherType> getOtherTypes() {
        if (otherTypes == null) {
            otherTypes = new ArrayList<OtherType>();
        }
        return this.otherTypes;
    }

    
    public void setExtension(ClassDescriptorExtension2 value) {
        this.extension = value;
    }
}
