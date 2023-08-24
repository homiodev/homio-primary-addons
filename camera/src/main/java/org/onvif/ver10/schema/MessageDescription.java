package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MessageDescription",
        propOrder = {"source", "key", "data", "extension"})
@XmlSeeAlso({org.onvif.ver10.schema.ConfigDescription.Messages.class})
public class MessageDescription {


    @Getter @XmlElement(name = "Source")
    protected ItemListDescription source;


    @Getter @XmlElement(name = "Key")
    protected ItemListDescription key;


    @Getter @XmlElement(name = "Data")
    protected ItemListDescription data;


    @Getter @XmlElement(name = "Extension")
    protected MessageDescriptionExtension extension;

    @XmlAttribute(name = "IsProperty")
    protected Boolean isProperty;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setSource(ItemListDescription value) {
        this.source = value;
    }


    public void setKey(ItemListDescription value) {
        this.key = value;
    }


    public void setData(ItemListDescription value) {
        this.data = value;
    }


    public void setExtension(MessageDescriptionExtension value) {
        this.extension = value;
    }


    public Boolean isIsProperty() {
        return isProperty;
    }


    public void setIsProperty(Boolean value) {
        this.isProperty = value;
    }

}
