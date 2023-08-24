package org.onvif.ver10.schema;

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
        name = "IOCapabilitiesExtension",
        propOrder = {"any", "auxiliary", "auxiliaryCommands", "extension"})
public class IOCapabilitiesExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "Auxiliary")
    protected Boolean auxiliary;

    @XmlElement(name = "AuxiliaryCommands")
    protected List<String> auxiliaryCommands;


    @Getter @XmlElement(name = "Extension", required = true)
    protected IOCapabilitiesExtension2 extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public Boolean isAuxiliary() {
        return auxiliary;
    }


    public void setAuxiliary(Boolean value) {
        this.auxiliary = value;
    }


    public List<String> getAuxiliaryCommands() {
        if (auxiliaryCommands == null) {
            auxiliaryCommands = new ArrayList<String>();
        }
        return this.auxiliaryCommands;
    }


    public void setExtension(IOCapabilitiesExtension2 value) {
        this.extension = value;
    }

}
