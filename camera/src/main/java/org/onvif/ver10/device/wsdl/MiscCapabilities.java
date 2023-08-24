







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MiscCapabilities")
public class MiscCapabilities {

    @XmlAttribute(name = "AuxiliaryCommands")
    protected List<String> auxiliaryCommands;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<String> getAuxiliaryCommands() {
        if (auxiliaryCommands == null) {
            auxiliaryCommands = new ArrayList<String>();
        }
        return this.auxiliaryCommands;
    }

}
