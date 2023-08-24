package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "IOCapabilities",
        propOrder = {"inputConnectors", "relayOutputs", "extension"})
public class IOCapabilities {


    @XmlElement(name = "InputConnectors")
    protected Integer inputConnectors;


    @XmlElement(name = "RelayOutputs")
    protected Integer relayOutputs;


    @XmlElement(name = "Extension")
    protected IOCapabilitiesExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setInputConnectors(Integer value) {
        this.inputConnectors = value;
    }


    public void setRelayOutputs(Integer value) {
        this.relayOutputs = value;
    }


    public void setExtension(IOCapabilitiesExtension value) {
        this.extension = value;
    }

}
