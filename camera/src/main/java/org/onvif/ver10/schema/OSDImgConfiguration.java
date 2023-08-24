package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDImgConfiguration",
        propOrder = {"imgPath", "extension"})
public class OSDImgConfiguration {


    @XmlElement(name = "ImgPath", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String imgPath;


    @XmlElement(name = "Extension")
    protected OSDImgConfigurationExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setImgPath(String value) {
        this.imgPath = value;
    }


    public void setExtension(OSDImgConfigurationExtension value) {
        this.extension = value;
    }

}
