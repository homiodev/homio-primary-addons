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
        name = "OSDConfigurationOptions",
        propOrder = {
                "maximumNumberOfOSDs",
                "type",
                "positionOption",
                "textOption",
                "imageOption",
                "extension"
        })
public class OSDConfigurationOptions {


    @Getter @XmlElement(name = "MaximumNumberOfOSDs", required = true)
    protected MaximumNumberOfOSDs maximumNumberOfOSDs;

    @XmlElement(name = "Type", required = true)
    protected List<OSDType> type;

    @XmlElement(name = "PositionOption", required = true)
    protected List<String> positionOption;


    @Getter @XmlElement(name = "TextOption")
    protected OSDTextOptions textOption;


    @Getter @XmlElement(name = "ImageOption")
    protected OSDImgOptions imageOption;


    @Getter @XmlElement(name = "Extension")
    protected OSDConfigurationOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setMaximumNumberOfOSDs(MaximumNumberOfOSDs value) {
        this.maximumNumberOfOSDs = value;
    }


    public List<OSDType> getType() {
        if (type == null) {
            type = new ArrayList<OSDType>();
        }
        return this.type;
    }


    public List<String> getPositionOption() {
        if (positionOption == null) {
            positionOption = new ArrayList<String>();
        }
        return this.positionOption;
    }


    public void setTextOption(OSDTextOptions value) {
        this.textOption = value;
    }


    public void setImageOption(OSDImgOptions value) {
        this.imageOption = value;
    }


    public void setExtension(OSDConfigurationOptionsExtension value) {
        this.extension = value;
    }

}
