package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "StorageReferencePath",
        propOrder = {"storageToken", "relativePath", "extension"})
public class StorageReferencePath {


    @XmlElement(name = "StorageToken", required = true)
    protected String storageToken;


    @XmlElement(name = "RelativePath")
    protected String relativePath;


    @XmlElement(name = "Extension")
    protected StorageReferencePathExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setStorageToken(String value) {
        this.storageToken = value;
    }


    public void setRelativePath(String value) {
        this.relativePath = value;
    }


    public void setExtension(StorageReferencePathExtension value) {
        this.extension = value;
    }

}
