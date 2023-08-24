package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZNode",
        propOrder = {
                "name",
                "supportedPTZSpaces",
                "maximumNumberOfPresets",
                "homeSupported",
                "auxiliaryCommands",
                "extension"
        })
public class PTZNode extends DeviceEntity {


    @Getter @XmlElement(name = "Name")
    protected String name;


    @Getter @XmlElement(name = "SupportedPTZSpaces", required = true)
    protected PTZSpaces supportedPTZSpaces;


    @Getter @XmlElement(name = "MaximumNumberOfPresets")
    protected int maximumNumberOfPresets;


    @Getter @XmlElement(name = "HomeSupported")
    protected boolean homeSupported;

    @XmlElement(name = "AuxiliaryCommands")
    protected List<String> auxiliaryCommands;


    @Getter @XmlElement(name = "Extension")
    protected PTZNodeExtension extension;

    @XmlAttribute(name = "FixedHomePosition")
    protected Boolean fixedHomePosition;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setName(String value) {
        this.name = value;
    }


    public void setSupportedPTZSpaces(PTZSpaces value) {
        this.supportedPTZSpaces = value;
    }


    public void setMaximumNumberOfPresets(int value) {
        this.maximumNumberOfPresets = value;
    }


    public void setHomeSupported(boolean value) {
        this.homeSupported = value;
    }


    public List<String> getAuxiliaryCommands() {
        if (auxiliaryCommands == null) {
            auxiliaryCommands = new ArrayList<String>();
        }
        return this.auxiliaryCommands;
    }


    public void setExtension(PTZNodeExtension value) {
        this.extension = value;
    }


    public Boolean isFixedHomePosition() {
        return fixedHomePosition;
    }


    public void setFixedHomePosition(Boolean value) {
        this.fixedHomePosition = value;
    }

}
