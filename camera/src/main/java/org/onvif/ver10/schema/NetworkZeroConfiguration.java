package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "NetworkZeroConfiguration",
        propOrder = {"interfaceToken", "enabled", "addresses", "extension"})
public class NetworkZeroConfiguration {


    @Getter @XmlElement(name = "InterfaceToken", required = true)
    protected String interfaceToken;


    @Getter @XmlElement(name = "Enabled")
    protected boolean enabled;

    @XmlElement(name = "Addresses")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected List<String> addresses;


    @Getter @XmlElement(name = "Extension")
    protected NetworkZeroConfigurationExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setInterfaceToken(String value) {
        this.interfaceToken = value;
    }


    public void setEnabled(boolean value) {
        this.enabled = value;
    }


    public List<String> getAddresses() {
        if (addresses == null) {
            addresses = new ArrayList<String>();
        }
        return this.addresses;
    }


    public void setExtension(NetworkZeroConfigurationExtension value) {
        this.extension = value;
    }

}
