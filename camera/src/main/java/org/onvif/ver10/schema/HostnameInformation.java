package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "HostnameInformation",
        propOrder = {"fromDHCP", "name", "extension"})
public class HostnameInformation {


    @XmlElement(name = "FromDHCP")
    protected boolean fromDHCP;


    @XmlElement(name = "Name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String name;


    @XmlElement(name = "Extension")
    protected HostnameInformationExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setFromDHCP(boolean value) {
        this.fromDHCP = value;
    }


    public void setName(String value) {
        this.name = value;
    }


    public void setExtension(HostnameInformationExtension value) {
        this.extension = value;
    }

}
