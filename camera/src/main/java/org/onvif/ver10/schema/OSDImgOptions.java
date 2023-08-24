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
        name = "OSDImgOptions",
        propOrder = {"imagePath", "extension"})
public class OSDImgOptions {

    @XmlElement(name = "ImagePath", required = true)
    @XmlSchemaType(name = "anyURI")
    protected List<String> imagePath;


    @Getter @XmlElement(name = "Extension")
    protected OSDImgOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<String> getImagePath() {
        if (imagePath == null) {
            imagePath = new ArrayList<String>();
        }
        return this.imagePath;
    }


    public void setExtension(OSDImgOptionsExtension value) {
        this.extension = value;
    }

}
